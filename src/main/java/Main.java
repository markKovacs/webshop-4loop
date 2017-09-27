import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.*;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

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

        // ROUTING (start with specific routes)

        Gson gson = new Gson();

        post("/api/add-to-cart", (req, res) -> ProductController.addToCart(req, res), gson::toJson);
        
        get("/", ProductController::renderProducts, new ThymeleafTemplateEngine());
        // EQUIVALENT WITH ABOVE
        get("/index", (Request req, Response res) -> {

            System.out.println("HI");
            return new ThymeleafTemplateEngine().render( ProductController.renderProducts(req, res) );
        });
        post("/index", (req, res) -> {
            System.out.println("HI");
            System.out.println("Size: " + req.queryParams().size());
            for (int i=0; i < req.queryParams().size(); i++){
                System.out.println(req.queryParams().toArray()[i] + ": " + req.queryParams(req.queryParams().toArray()[i].toString()));
            }
            return "";
        });

    }

    public static void populateData() {
        // This method initializes the data and loads into memory storage.

        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

        //setting up a new supplier
        Supplier lucas = new Supplier("Lucasarts", "Movie and dream making");
        supplierDataStore.add(lucas);
        Supplier fox = new Supplier("20th Century Fox", "Movie making");
        supplierDataStore.add(fox);
        Supplier uffizi = new Supplier("Uffizi", "Museum in Italy");
        supplierDataStore.add(uffizi);
        Supplier columbia = new Supplier("Columbia Pictures", "Movie making");
        supplierDataStore.add(columbia);
        Supplier newLineCinema = new Supplier("New Line Cinema", "Movie making");
        supplierDataStore.add(newLineCinema);
        Supplier orion = new Supplier("Orion Pictures", "Movie making, change the future");
        supplierDataStore.add(orion);
        Supplier cbs = new Supplier("CBS", "Television series");
        supplierDataStore.add(cbs);
        Supplier whiteStarLine = new Supplier("White Star Line", "Ship building");
        supplierDataStore.add(whiteStarLine);
        Supplier emi = new Supplier("EMI Music", "CD and LP publishing etc.");
        supplierDataStore.add(emi);
        Supplier sportMuseum = new Supplier("Sport Museum", "Sport equipment");
        supplierDataStore.add(sportMuseum);
        Supplier hungary = new Supplier("State of Hungary", "Our beautiful homeland");
        supplierDataStore.add(hungary);
        Supplier louvre = new Supplier("Louvre", "Museum in Paris, France");
        supplierDataStore.add(louvre);
        Supplier pentagon = new Supplier("Pentagon", "The headquarters of the US Department of Defense");
        supplierDataStore.add(pentagon);


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
        productDataStore.add(new Product("Neymar's shoes", 170, "USD", "Neymar scored 4 goals against Mozambique in this special sport equipment.", famous, louvre, "neymar.jpg"));
        productDataStore.add(new Product("Mohamed Ali's boxing gloves", 800, "USD", "Ali won against a lot of opponents in this gloves.", famous, sportMuseum, "ali.jpg"));
        productDataStore.add(new Product("Zsolt Erdei's boxing gloves", 100, "USD", "Erdei won against a lot of opponents in this gloves.", famous, sportMuseum, "erdei.jpg"));
        productDataStore.add(new Product("Federer's tennis racket", 200, "USD", "Federer won his last US Open with this racket.", famous, sportMuseum, "federer.jpg"));
        productDataStore.add(new Product("The piano of John Lennon", 500, "USD", "Lennon played Imagine on this instrument.", famous, emi, "lennon.jpg"));
        productDataStore.add(new Product("The shirt of Ferenc Puskas", 820, "USD", "Puskas scored 3 goals against England in this jersey.", famous, sportMuseum, "puskas.jpg"));
        productDataStore.add(new Product("Mona Lisa", 3000, "USD", "Leonardo's original painting, it's a very exclusive offer.", historical, louvre, "monalisa.jpg"));
        productDataStore.add(new Product("Sebastian Vettel's helmet", 330, "USD", "Vettel won 4 Formula-1 GP with this helmet.", famous, sportMuseum, "vettel.jpg"));
        productDataStore.add(new Product("Brian May's Red Special", 1000, "USD", "A guitar made by the famous member of Queen, he played in this instrument for example We Will Rock You.", famous, emi, "brianmay.jpg"));

    }

}
