package com.codecool.shop;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.*;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.order.InputField;
import com.google.gson.Gson;
import spark.Filter;
import jdk.internal.util.xml.impl.Input;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // SERVER SETTINGS
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        // ENABLE DEBUG SCREEN
        enableDebugScreen();

        // POPULATE DATA FOR MEMORY STORAGE
        populateData();

        // BEFORE REQUEST CHECK
        //before("/*", orderInProgressFilter());

        // API ENDPOINTS
        Gson gson = new Gson();
        post("/api/add-to-cart", ProductController::addToCart, gson::toJson);

        // ROUTING (start with specific routes)
        get("/payment", ProductController::renderPayment, new ThymeleafTemplateEngine());

        get("/payment/bank", ProductController:: renderBankPayment, new ThymeleafTemplateEngine());

        get("/payment/paypal", ProductController:: renderPayPalPayment, new ThymeleafTemplateEngine());

        post("/payment/bank", (request, response) -> {
            // TODO: check if this is correct, test, verify
            String statusMessage = ProductController.payWithCreditCard(request, response);
            if (statusMessage.equals("success")) {
                response.redirect("/payment/success");
            } else {
                response.redirect("/payment/bank");
            }

            return null;
        });

        post("/payment/paypal", (request, response) -> {
            // TODO: check if this is correct, test, verify
            String statusMessage = ProductController.payWithPayPal(request, response);
            if (statusMessage.equals("success")) {
                response.redirect("/payment/success");
            } else {
                response.redirect("/payment/paypal");
            }

            return null;
        });

        get("/payment/success", ProductController:: renderSuccess, new ThymeleafTemplateEngine());

        get("/", ProductController::renderProducts, new ThymeleafTemplateEngine());
        // EQUIVALENT WITH ABOVE
        get("/index", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render( ProductController.renderProducts(req, res) );
        });
        post("/index", (req, res) -> {
            for (int i=0; i < req.queryParams().size(); i++){
                System.out.println(req.queryParams().toArray()[i] + ": " + req.queryParams(req.queryParams().toArray()[i].toString()));
            }
            return new ThymeleafTemplateEngine().render( ProductController.renderProductsBySupplier(req, res) );
        });

        get("/checkout", (Request req, Response res) -> {
            List errorMessages = new ArrayList();
            Map userDatas = new HashMap();
            Map params = new HashMap();
            params.put("user", userDatas);
            params.put("errors", errorMessages);
            return new ThymeleafTemplateEngine().render(new ModelAndView(params, "checkout"));
        });

        post("/checkout", (Request req, Response res) -> {
            Map userDatas = new HashMap();
            userDatas.put("username", req.queryParams("username"));
            userDatas.put("email", req.queryParams("email"));
            userDatas.put("phone", req.queryParams("phone"));
            userDatas.put("billcountry", req.queryParams("bill-country"));
            userDatas.put("billcity", req.queryParams("bill-city"));
            userDatas.put("billzip", req.queryParams("bill-zip"));
            userDatas.put("billaddress", req.queryParams("bill-address"));
            userDatas.put("shipcountry", req.queryParams("ship-country"));
            userDatas.put("shipcity", req.queryParams("ship-city"));
            userDatas.put("shipzip", req.queryParams("ship-zip"));
            userDatas.put("shipaddress", req.queryParams("ship-address"));

            List errorMessages = new ArrayList();
            if (InputField.FULL_NAME.validate(userDatas.get("username").toString()) == false){
                errorMessages.add("Invalid username.");
            }
            if (InputField.EMAIL.validate(userDatas.get("email").toString()) == false){
                errorMessages.add("Invalid email address");
            }
            if (InputField.PHONE.validate(req.queryParams("phone")) == false){
                errorMessages.add("Invalid phone number");
            }
            if (InputField.COUNTRY.validate(userDatas.get("billcountry").toString()) == false){
                errorMessages.add("Invalid billing country");
            }
            if (InputField.CITY.validate(userDatas.get("billcity").toString()) == false){
                errorMessages.add("Invalid billing city");
            }
            if (InputField.ZIP_CODE.validate(userDatas.get("billzip").toString()) == false){
                errorMessages.add("Invalid billing ZIP code");
            }
            if (InputField.ADDRESS.validate(userDatas.get("billaddress").toString()) == false){
                errorMessages.add("Invalid billing address");
            }
            if (InputField.COUNTRY.validate(userDatas.get("shipcountry").toString()) == false){
                errorMessages.add("Invalid shipping country");
            }
            if (InputField.CITY.validate(userDatas.get("shipcity").toString()) == false){
                errorMessages.add("Invalid shipping city");
            }
            if (InputField.ZIP_CODE.validate(userDatas.get("shipzip").toString()) == false){
                errorMessages.add("Invalid shipping ZIP code");
            }
            if (InputField.ADDRESS.validate(userDatas.get("shipaddress").toString()) == false){
                errorMessages.add("Invalid shipping address");
            }

            if (errorMessages.size() > 0){
                Map params = new HashMap();
                params.put("user", userDatas);
                params.put("errors", errorMessages);
                return new ThymeleafTemplateEngine().render(new ModelAndView(params, "checkout"));
            } else {
                res.redirect("/payment");
            }

            return "";

        });

        get("/cart", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render( ProductController.reviewCart(req, res) );
        });

    }

    public static void populateData() {
        // This method initializes the data and loads into memory storage.

        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

        //setting up a new supplier
        Supplier cbs = new Supplier("CBS", "Television series");
        supplierDataStore.add(cbs);
        Supplier columbia = new Supplier("Columbia Pictures", "Movie making");
        supplierDataStore.add(columbia);
        Supplier emi = new Supplier("EMI Music", "CD and LP publishing etc.");
        supplierDataStore.add(emi);
        Supplier louvre = new Supplier("Louvre", "Museum in Paris, France");
        supplierDataStore.add(louvre);
        Supplier lucas = new Supplier("Lucasarts", "Movie and dream making");
        supplierDataStore.add(lucas);
        Supplier newLineCinema = new Supplier("New Line Cinema", "Movie making");
        supplierDataStore.add(newLineCinema);
        Supplier orion = new Supplier("Orion Pictures", "Movie making, change the future");
        supplierDataStore.add(orion);
        Supplier pentagon = new Supplier("Pentagon", "The headquarters of the US Department of Defense");
        supplierDataStore.add(pentagon);
        Supplier sportMuseum = new Supplier("Sport Museum", "Sport equipment");
        supplierDataStore.add(sportMuseum);
        Supplier hungary = new Supplier("State of Hungary", "Our beautiful homeland");
        supplierDataStore.add(hungary);
        Supplier uffizi = new Supplier("Uffizi", "Museum in Italy");
        supplierDataStore.add(uffizi);
        Supplier whiteStarLine = new Supplier("White Star Line", "Ship building");
        supplierDataStore.add(whiteStarLine);
        Supplier fox = new Supplier("20th Century Fox", "Movie making");
        supplierDataStore.add(fox);


        //setting up a new product category
        ProductCategory movies = new ProductCategory("Movies", "Entertainment", "Vehicles, weapons and other famous objects from great movies.");
        productCategoryDataStore.add(movies);

        ProductCategory historical = new ProductCategory("Historical", "Article", "Tangible memories from emperors, generals etc.");
        productCategoryDataStore.add(historical);

        ProductCategory famous = new ProductCategory("Famous Artifacts", "Article", "Tangible memories from popular musicians, athletes etc.");
        productCategoryDataStore.add(famous);


        //setting up products and printing it
        productDataStore.add(new Product("Luke's Lightsaber", 49.9f, "USD", "Fantastic price. Good ecosystem and controls. Helpful technical support.", movies, lucas, "lightsaber.jpg"));
        productDataStore.add(new Product("Bud Spencer's pan", 20, "USD", "Old tool from az old friend. Old tool from az old friend.", movies, columbia, "bud_pan.jpg"));
        productDataStore.add(new Product("The last soap from Fight Club", 25, "USD", "Be clean. Be a fighter. Be a weapon.", movies, fox, "soap.jpg"));
        productDataStore.add(new Product("The Ring", 3000, "USD", "One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.", movies, newLineCinema, "ring.jpg"));
        productDataStore.add(new Product("Carbonite Han Solo", 500, "USD", "This cold piece of carbonite was Jabba's favorite knick-knackery.", movies, lucas, "hansolo.jpg"));
        productDataStore.add(new Product("Jockey Ewing's whiskey cup", 200, "USD", "A glass cup from the famous oil tycoon.", movies, cbs, "whiskeycup.jpg"));
        productDataStore.add(new Product("Chewbacca's crossbow", 89, "USD", "Dependable weapon from a good guy.", movies, lucas, "chewbaccabow.jpg"));
        productDataStore.add(new Product("Terminator's endoskeleton", 2000, "USD", "Your new personal coach and trainer", movies, orion, "terminator.jpg"));
        productDataStore.add(new Product("AT-ST", 479, "USD", "Good old vehicle from the dark side.", movies, lucas, "atst.jpg"));
        productDataStore.add(new Product("The Helmet of Darth Vader", 500, "USD", "Black hat from the dark side.", movies, lucas, "vaderhelmet.jpg"));
        productDataStore.add(new Product("R2D2", 400, "USD", "Faithful personal assistant. Faithful personal assistant.", movies, lucas, "r2d2.jpg"));
        productDataStore.add(new Product("Millennium Falcon", 1500, "USD", "Good vehicle instead of a dull car.", movies, lucas, "falcon.jpg"));
        productDataStore.add(new Product("Titanic Wreck", 10000, "USD", "A sad wreck in the deep sea. A sad wreck in the deep sea. A sad wreck in the deep sea.", historical, whiteStarLine, "titanic.jpg"));
        productDataStore.add(new Product("The Dagger of the Killers of Iulius Caesar", 89, "USD", "This weapon was used by Brutus too. This weapon was used by Brutus too.", historical, uffizi, "dagger.jpg"));
        productDataStore.add(new Product("Death Star", 4000, "USD", "Your new flat. Your new flat. Your new flat. Your new flat.", movies, lucas, "deathstar.jpg"));
        productDataStore.add(new Product("The Holy Right", 3000, "USD", "The blissful hand of King Saint Stephen. The blissful hand of King Saint Stephen.", historical, hungary, "holyright.jpg"));
        productDataStore.add(new Product("The Horse of Attila the Hun", 3000, "USD", "Original and entire skeleton, it has every bones.", historical, hungary, "attila.jpg"));
        productDataStore.add(new Product("The Holy Crown", 3000, "USD", "Crown jewel from the ancient times of Hungary.", historical, hungary, "crown.jpg"));
        productDataStore.add(new Product("Elvis Jacket", 1230, "USD", "Elvis went on the stage in this jacket many times.", famous, emi, "elvis.jpg"));
        productDataStore.add(new Product("Donald Trump's necktie", 90, "USD", "Trump wore this necktie when he discussed some problems with Kim Jong-un.", famous, pentagon, "trump.jpg"));
        productDataStore.add(new Product("Anita Gorbicz's first ball", 200, "USD", "Gorbicz played her first handball match with this ball. She was only 6 years old.", famous, sportMuseum, "gorbicz.jpg"));
        productDataStore.add(new Product("Neymar's shoes", 170, "USD", "Neymar scored 4 goals against Mozambique in this special sport equipment.", famous, sportMuseum, "neymar.jpg"));
        productDataStore.add(new Product("Mohamed Ali's boxing gloves", 800, "USD", "Ali won against a lot of opponents in this gloves.", famous, sportMuseum, "ali.jpg"));
        productDataStore.add(new Product("Zsolt Erdei's boxing gloves", 100, "USD", "Erdei won against a lot of opponents in this gloves.", famous, sportMuseum, "erdei.jpg"));
        productDataStore.add(new Product("Federer's tennis racket", 200, "USD", "Federer won his last US Open with this racket.", famous, sportMuseum, "federer.jpg"));
        productDataStore.add(new Product("The piano of John Lennon", 500, "USD", "Lennon played Imagine on this instrument.", famous, emi, "lennon.jpg"));
        productDataStore.add(new Product("The shirt of Ferenc Puskas", 820, "USD", "Puskas scored 3 goals against England in this jersey.", famous, sportMuseum, "puskas.jpg"));
        productDataStore.add(new Product("Mona Lisa", 3000, "USD", "Leonardo's original painting, it's a very exclusive offer.", historical, louvre, "monalisa.jpg"));
        productDataStore.add(new Product("Sebastian Vettel's helmet", 330, "USD", "Vettel won 4 Formula-1 GP with this helmet.", famous, sportMuseum, "vettel.jpg"));
        productDataStore.add(new Product("Brian May's Red Special", 1000, "USD", "A guitar made by the famous member of Queen, he played in this instrument for example We Will Rock You.", famous, emi, "brianmay.jpg"));

    }

    public static Filter orderInProgressFilter() {
        return new Filter() {
            @Override
            public void handle(Request req, Response res) {
                int orderId = req.session().attribute("order_id") == null ? -1 :
                        Integer.valueOf(req.session().attribute("order_id")+"");

                if (orderId != -1) {
                    Order order = OrderDaoMem.getInstance().find(orderId);
                    Status status = order.getStatus();
                    if (status.equals(Status.REVIEWED) && !req.pathInfo().equals("/checkout")) {
                        res.redirect("/checkout");
                    } else if (status.equals(Status.CHECKEDOUT) && !req.pathInfo().equals("/payment")) {
                        res.redirect("/payment");
                    } else if (status.equals(Status.NEW) &&
                            (req.pathInfo().equals("/checkout") || req.pathInfo().equals("/payment"))) {
                        res.redirect("/?error=bad");
                    }
                } else if (req.pathInfo().equals("/checkout") || req.pathInfo().equals("/payment")) {
                    res.redirect("/?error=bad");
                }
            }
        };
    }

}
