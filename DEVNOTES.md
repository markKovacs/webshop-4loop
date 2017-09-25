# Development Notes

### Changes 25 Sep, 2017
- Gitignore updated:
    + Maven, Java, Java-Web, IntelliJ+iml are included.
    
- Dependencies added:
    + nz.net.ultraq.thymeleaf : thymeleaf-layout-dialect : 2.2.2
    + org.thymeleaf.extras : thymeleaf-extras-java8time : 3.0.0.RELEASE

- Dependency version changes (listed as new):
    + org.thymeleaf : thymeleaf : 3.0.5.RELEASE

- ThymeleafTemplateEngine Class
    + Now implemented locally.
    + This version is newer than original and enables us to add dialects.
    + initialize() method makes use of dialects as follows:
        - templateEngine.addDialect(new Java8TimeDialect());
        - templateEngine.addDialect(new LayoutDialect());
    + Call constructor the same way from main as before.
    + Some setting to note:
        - Engine is being told to look for template in 'template' folder (inside resources).
        - '.html' is being added automatically to template names as handled here.

- Layout Dialect:
    + Added to ThymeleafTemplateEngine Class as mentioned above.
    + layout.html serves the purpose of the base template.
        - all other templates inherit from it
    + In layout.html, content from child templates will be inserted to:
        - div class="container" layout:fragment="content" div
    + Children templates will need to declare the following inside html opening tag:
        - layout:decorate="~{layout}"
        - 'layout:decorate' tells engine to decorate child template
        - '~{layout}' means to look for layout.html in the same folder to inherit from
        - title will be rendered as pattern:
            + $CONTENT_TITLE - $LAYOUT_TITLE

- JavaScript Files
    + JS files created with js folder in resources/public
    + JS files being imported in layout.html in the right order
    + JS files use the 'app' object as a skeleton, to insulate namespaces.

- HTML Files
    + New HTML files created, all inheriting from layout.html:
        - checkout.html
        - payment.html
        - review.html
        - success.html
