import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.*;
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

        get("/hello", (req, res) -> "Hello World");

        get("/", ProductController::renderProducts, new ThymeleafTemplateEngine());
        // EQUIVALENT WITH ABOVE
        get("/index", (Request req, Response res) -> {
           return new ThymeleafTemplateEngine().render( ProductController.renderProducts(req, res) );
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



        //setting up a new product category
        ProductCategory movies = new ProductCategory("Movies", "Entertainment", "Vehicles, weapons and other famous objects from great movies.");
        productCategoryDataStore.add(movies);

        ProductCategory historical = new ProductCategory("Historical", "Article", "Tangible memories from great persons.");
        productCategoryDataStore.add(historical);


        //setting up products and printing it
        productDataStore.add(new Product("Luke's Lightsaber", 49.9f, "USD", "Fantastic price. Good ecosystem and controls. Helpful technical support.", movies, lucas, "lightsaber.jpg"));
        productDataStore.add(new Product("Bud Spencer's pan", 20, "USD", "Old tool from az old friend. Old tool from az old friend.", movies, columbia, "bud_pan.jpg"));
        productDataStore.add(new Product("The last soap from Fight Club", 25, "USD", "Be clean. Be a fighter. Be a weapon.", movies, fox, "soap.jpg"));
        productDataStore.add(new Product("The Ring", 3000, "USD", "One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.", movies, newLineCinema, "ring.jpg"));
        productDataStore.add(new Product("AT-ST", 479, "USD", "Good old vehicle from the dark side.", movies, lucas, "atst.jpg"));
        productDataStore.add(new Product("Jockey Ewing's whiskey cup", 200, "USD", "A glass cup from the famous oil tycoon.", movies, cbs, "whiskeycup.jpg"));
        productDataStore.add(new Product("Chewbacca's crossbow", 89, "USD", "Dependable weapon from a good guy.", movies, lucas, "chewbaccabow.jpg"));
        productDataStore.add(new Product("Terminator's endoskeleton", 2000, "USD", "Your new personal coach and trainer", movies, orion, "terminator.jpg"));
        productDataStore.add(new Product("Death Star", 4000, "USD", "Your new flat. Your new flat. Your new flat. Your new flat.", movies, lucas, "deathstar.jpg"));
        productDataStore.add(new Product("The Helmet of Darth Vader", 500, "USD", "Black hat from the dark side.", movies, lucas, "vaderhelmet.jpg"));
        productDataStore.add(new Product("R2D2", 400, "USD", "Faithful personal assistant. Faithful personal assistant.", movies, lucas, "r2d2.jpg"));
        productDataStore.add(new Product("Millennium Falcon", 1500, "USD", "Good vehicle instead of a dull car.", movies, lucas, "falcon.jpg"));
        productDataStore.add(new Product("Titanic Wreck", 10000, "USD", "A sad wreck in the deep sea. A sad wreck in the deep sea. A sad wreck in the deep sea.", historical, whiteStarLine, "titanic.jpg"));
        productDataStore.add(new Product("The dagger, which killed Iulius Caesar", 89, "USD", "This weapon was used by Brutus too. This weapon was used by Brutus too.", historical, uffizi, "dagger.jpg"));


    }

}
