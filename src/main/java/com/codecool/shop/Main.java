package com.codecool.shop;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.codecool.shop.controller.AccountController;
import com.codecool.shop.controller.OrderController;
import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.*;
import com.codecool.shop.model.*;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.google.gson.Gson;
import spark.Filter;
import spark.Request;
import spark.Response;

public class Main {

    // GLOBALS
    public static float balanceInUSD = 75_000.0f;

    public static void main(String[] args) {

        // DATABASE
        Config.USE_PRODUCTION_DB = true;

        // SERVER SETTINGS
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        // ENABLE DEBUG SCREEN
        enableDebugScreen();

        // POPULATE DATA FOR MEMORY STORAGE
        if (!Config.USE_DB) { populateData(); }

        // BEFORE REQUEST CHECK
        before(orderInProgressFilter);

        // API ENDPOINTS
        Gson gson = new Gson();
        post("/api/add-to-cart", OrderController::handleAddToCart, gson::toJson);
        post("/api/change-product-quantity", OrderController::changeQuantity, gson::toJson);
        post("/api/removeOrder-line-item", OrderController::removeLineItem, gson::toJson);

        // ROUTING (sprint #1)
        get("/payment", OrderController::renderPayment, new ThymeleafTemplateEngine());
        get("/payment/bank", OrderController::renderBankPayment, new ThymeleafTemplateEngine());
        get("/payment/paypal", OrderController::renderPayPalPayment, new ThymeleafTemplateEngine());
        post("/payment/bank", OrderController::payWithChosenMethod, new ThymeleafTemplateEngine());
        post("/payment/paypal", OrderController::payWithChosenMethod, new ThymeleafTemplateEngine());
        get("/payment/success", OrderController::renderSuccess, new ThymeleafTemplateEngine());

        get("/checkout", OrderController::renderCheckout, new ThymeleafTemplateEngine());
        post("/checkout", OrderController::doCheckout, new ThymeleafTemplateEngine());

        get("/cart", OrderController::renderReview, new ThymeleafTemplateEngine());
        post("/cart", OrderController::finalizeOrder, new ThymeleafTemplateEngine());

        get("/", ProductController::renderProducts, new ThymeleafTemplateEngine());

        // ROUTING (sprint #2)
        get("/register", AccountController::renderRegistration, new ThymeleafTemplateEngine());
        get("/login", AccountController::renderLogin, new ThymeleafTemplateEngine());
        get("/history", AccountController::renderOrderHistory, new ThymeleafTemplateEngine());
        get("/profile", AccountController::renderProfile, new ThymeleafTemplateEngine());

        post("/register", AccountController::doRegistration, new ThymeleafTemplateEngine());
        post("/login", AccountController::doLogin, new ThymeleafTemplateEngine());
        post("/logout", AccountController::doLogout, new ThymeleafTemplateEngine());
        post("/profile", AccountController::editProfile, new ThymeleafTemplateEngine());

    }

    private static Filter orderInProgressFilter = (Request req, Response res) -> {
        // TODO: if not logged in, allow route only to /, /login and /register
        // TODO: if logged in, try to get order, rest is the same, but allow /logout

        boolean loggedIn = req.session().attribute("user_id") != null;
        if (!loggedIn) {
            if (!(req.pathInfo().equals("/") || req.pathInfo().equals("/login") || req.pathInfo().equals("/register"))) {
                res.redirect("/login");
                halt(401);
            } else {
                return;
            }
        }

        Order order = null;
        if (req.session().attribute("user_id") != null) {
            int userId = req.session().attribute("user_id");
            order = DaoFactory.getOrderDao().findOpenByUserId(userId);
        }

        if (order != null) {
            Status status = order.getStatus();
            switch (status) {
                case NEW:
                    if (!(req.pathInfo().equals("/") || req.pathInfo().equals("/cart") ||
                            req.pathInfo().startsWith("/api/") ||
                            req.pathInfo().equals("/logout") ||
                            req.pathInfo().equals("/history") ||
                            req.pathInfo().equals("/profile") ||
                            (req.pathInfo().equals("/checkout") && req.queryParams("next") != null))) {
                        res.redirect("/?error=restricted");
                        halt(401);
                    }
                    break;
                case REVIEWED:
                    if (!(req.pathInfo().equals("/checkout") ||
                            req.pathInfo().equals("/logout") ||
                            (req.pathInfo().equals("/cart") && req.queryParams("back") != null) ||
                            (req.pathInfo().equals("/payment") && req.queryParams("next") != null))) {
                        res.redirect("/checkout");
                        halt(401);
                    }
                    break;
                case CHECKEDOUT:
                    if ( !(req.pathInfo().equals("/payment") ||
                            req.pathInfo().equals("/logout") ||
                            req.pathInfo().equals("/payment/bank") ||
                            req.pathInfo().equals("/payment/paypal") ||
                            (req.pathInfo().equals("/checkout") && req.queryParams("back") != null) ) ) {
                        res.redirect("/payment");
                        halt(401);
                    }
                    break;
            }
        } else if (!(req.pathInfo().equals("/") || req.pathInfo().equals("/cart") ||
                req.pathInfo().equals("/logout") ||
                req.pathInfo().equals("/history") ||
                req.pathInfo().equals("/profile") ||
                req.pathInfo().equals("/api/add-to-cart") ||
                req.pathInfo().equals("/payment/success"))) {
            res.redirect("/?error=restricted");
            halt(401);
        }

    };

    private static void populateData() {
        // This method initializes the data and loads into memory storage.

        ProductDao productDao = DaoFactory.getProductDao();
        ProductCategoryDao productCategoryDao = DaoFactory.getProductCategoryDao();
        SupplierDao supplierDao = DaoFactory.getSupplierDao();

        //setting up a new supplier
        Supplier cbs = new Supplier("CBS", "Television series");
        supplierDao.add(cbs);
        Supplier columbia = new Supplier("Columbia Pictures", "Movie making");
        supplierDao.add(columbia);
        Supplier emi = new Supplier("EMI Music", "CD and LP publishing etc.");
        supplierDao.add(emi);
        Supplier louvre = new Supplier("Louvre", "Museum in Paris, France");
        supplierDao.add(louvre);
        Supplier lucas = new Supplier("Lucasarts", "Movie and dream making");
        supplierDao.add(lucas);
        Supplier newLineCinema = new Supplier("New Line Cinema", "Movie making");
        supplierDao.add(newLineCinema);
        Supplier orion = new Supplier("Orion Pictures", "Movie making, change the future");
        supplierDao.add(orion);
        Supplier pentagon = new Supplier("Pentagon", "The headquarters of the US Department of Defense");
        supplierDao.add(pentagon);
        Supplier sportMuseum = new Supplier("Sport Museum", "Sport equipment");
        supplierDao.add(sportMuseum);
        Supplier hungary = new Supplier("State of Hungary", "Our beautiful homeland");
        supplierDao.add(hungary);
        Supplier uffizi = new Supplier("Uffizi", "Museum in Italy");
        supplierDao.add(uffizi);
        Supplier whiteStarLine = new Supplier("White Star Line", "Ship building");
        supplierDao.add(whiteStarLine);
        Supplier fox = new Supplier("20th Century Fox", "Movie making");
        supplierDao.add(fox);


        //setting up a new product category
        ProductCategory movies = new ProductCategory("Movies", "Entertainment", "Vehicles, weapons and other famous objects from great movies.");
        productCategoryDao.add(movies);

        ProductCategory historical = new ProductCategory("Historical", "Article", "Tangible memories from emperors, generals etc.");
        productCategoryDao.add(historical);

        ProductCategory famous = new ProductCategory("Famous Artifacts", "Article", "Tangible memories from popular musicians, athletes etc.");
        productCategoryDao.add(famous);

        //setting up products and printing it
        productDao.add(new Product("Luke's Lightsaber", 49.9f, "USD", "Fantastic price. Good ecosystem and controls. Helpful technical support.", movies, lucas, "lightsaber.jpg"));
        productDao.add(new Product("Bud Spencer's pan", 20, "USD", "Old tool from az old friend. Old tool from az old friend.", movies, columbia, "bud_pan.jpg"));
        productDao.add(new Product("The last soap from Fight Club", 25, "USD", "Be clean. Be a fighter. Be a weapon.", movies, fox, "soap.jpg"));
        productDao.add(new Product("The Ring", 3000, "USD", "One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.", movies, newLineCinema, "ring.jpg"));
        productDao.add(new Product("Carbonite Han Solo", 500, "USD", "This cold piece of carbonite was Jabba's favorite knick-knackery.", movies, lucas, "hansolo.jpg"));
        productDao.add(new Product("Jockey Ewing's whiskey cup", 200, "USD", "A glass cup from the famous oil tycoon.", movies, cbs, "whiskeycup.jpg"));
        productDao.add(new Product("Chewbacca's crossbow", 89, "USD", "Dependable weapon from a good guy.", movies, lucas, "chewbaccabow.jpg"));
        productDao.add(new Product("Terminator's endoskeleton", 2000, "USD", "Your new personal coach and trainer", movies, orion, "terminator.jpg"));
        productDao.add(new Product("AT-ST", 479, "USD", "Good old vehicle from the dark side.", movies, lucas, "atst.jpg"));
        productDao.add(new Product("The Helmet of Darth Vader", 500, "USD", "Black hat from the dark side.", movies, lucas, "vaderhelmet.jpg"));
        productDao.add(new Product("R2D2", 400, "USD", "Faithful personal assistant. Faithful personal assistant.", movies, lucas, "r2d2.jpg"));
        productDao.add(new Product("Millennium Falcon", 1500, "USD", "Good vehicle instead of a dull car.", movies, lucas, "falcon.jpg"));
        productDao.add(new Product("Titanic Wreck", 10000, "USD", "A sad wreck in the deep sea. A sad wreck in the deep sea. A sad wreck in the deep sea.", historical, whiteStarLine, "titanic.jpg"));
        productDao.add(new Product("The Dagger of the Killers of Iulius Caesar", 89, "USD", "This weapon was used by Brutus too. This weapon was used by Brutus too.", historical, uffizi, "dagger.jpg"));
        productDao.add(new Product("Death Star", 4000, "USD", "Your new flat. Your new flat. Your new flat. Your new flat.", movies, lucas, "deathstar.jpg"));
        productDao.add(new Product("The Holy Right", 3000, "USD", "The blissful hand of King Saint Stephen. The blissful hand of King Saint Stephen.", historical, hungary, "holyright.jpg"));
        productDao.add(new Product("The Horse of Attila the Hun", 3000, "USD", "Original and entire skeleton, it has every bones.", historical, hungary, "attila.jpg"));
        productDao.add(new Product("The Holy Crown", 3000, "USD", "Crown jewel from the ancient times of Hungary.", historical, hungary, "crown.jpg"));
        productDao.add(new Product("Elvis Jacket", 1230, "USD", "Elvis went on the stage in this jacket many times.", famous, emi, "elvis.jpg"));
        productDao.add(new Product("Donald Trump's necktie", 90, "USD", "Trump wore this necktie when he discussed some problems with Kim Jong-un.", famous, pentagon, "trump.jpg"));
        productDao.add(new Product("Anita Gorbicz's first ball", 200, "USD", "Gorbicz played her first handball match with this ball. She was only 6 years old.", famous, sportMuseum, "gorbicz.jpg"));
        productDao.add(new Product("Neymar's shoes", 170, "USD", "Neymar scored 4 goals against Mozambique in this special sport equipment.", famous, sportMuseum, "neymar.jpg"));
        productDao.add(new Product("Mohamed Ali's boxing gloves", 800, "USD", "Ali won against a lot of opponents in this gloves.", famous, sportMuseum, "ali.jpg"));
        productDao.add(new Product("Zsolt Erdei's boxing gloves", 100, "USD", "Erdei won against a lot of opponents in this gloves.", famous, sportMuseum, "erdei.jpg"));
        productDao.add(new Product("Federer's tennis racket", 200, "USD", "Federer won his last US Open with this racket.", famous, sportMuseum, "federer.jpg"));
        productDao.add(new Product("The piano of John Lennon", 500, "USD", "Lennon played Imagine on this instrument.", famous, emi, "lennon.jpg"));
        productDao.add(new Product("The shirt of Ferenc Puskas", 820, "USD", "Puskas scored 3 goals against England in this jersey.", famous, sportMuseum, "puskas.jpg"));
        productDao.add(new Product("Mona Lisa", 3000, "USD", "Leonardo's original painting, it's a very exclusive offer.", historical, louvre, "monalisa.jpg"));
        productDao.add(new Product("Sebastian Vettel's helmet", 330, "USD", "Vettel won 4 Formula-1 GP with this helmet.", famous, sportMuseum, "vettel.jpg"));
        productDao.add(new Product("Brian May's Red Special", 1000, "USD", "A guitar made by the famous member of Queen, he played in this instrument for example We Will Rock You.", famous, emi, "brianmay.jpg"));
    }

}
