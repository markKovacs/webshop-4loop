
var app = app || {};

app.productLogic = {

    addToCartListener: function () {
        $("#products").on("click", ".add-to-cart", function(ev) {
            ev.stopPropagation();
            var quantity = $(this).prev().val();
            if (quantity < 1 || quantity > 99) {
                app.utils.toastMessage("Wrong quantity. Must be between 1-99.");
                return;
            }
            var productId = $(this).data("product-id");
            app.dataHandler.addToCart(productId, quantity);
        });
    },

    handleAddToCartSuccess: function (response) {
        // TODO: DOM manipulation of cart item counter and quantity input reset to 1
        app.utils.toastMessage("Successfully added to cart.");
    },

    handleAddToCartError: function (response) {
        // TODO: quantity input reset to 1
        app.utils.toastMessage("Failed to add to cart.");
    },

    resetQuantityInput: function () {
        // TODO: implement here
    }

};