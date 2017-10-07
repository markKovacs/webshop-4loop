
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

    reviewQuantityChangeListener: function () {
        $(".review-quantity").on("change", function(ev) {
            ev.stopPropagation();
            var quantity = $(this).val();
            if (quantity < 0 || quantity > 99) {
                app.utils.toastMessage("Wrong quantity. Must be between 1-99.");
                return;
            }
            var productId = $(this).data("product-id");
            app.dataHandler.setProductQuantity(productId, quantity);
        });
    },

    reviewRemoveItemListener: function () {
        $(".remove-img").on("click", function(ev) {
            ev.stopPropagation();
            var productId = $(this).data("product-id");
            app.dataHandler.removeLineItem(productId);
        });
    },

    handleAddToCartSuccess: function (response, productId) {
        app.utils.toastMessage("Successfully added to cart.");
        if (response === "new_item") {
            var cartItemCount = $("#cart-item-count");
            cartItemCount.text(Number(cartItemCount.text()) + 1);
        }
        this.resetQuantityInput(productId);
    },

    handleAddToCartError: function (productId) {
        app.utils.toastMessage("Failed to add to cart.");
        this.resetQuantityInput(productId);
    },

    resetQuantityInput: function (productId) {
        $('#product-id-' + productId).children().first().val(1);
    },

    handleChangeProductQuantitySuccess: function (response, productId) {
        var resp = JSON.parse(response);
        $("#total-total").text(String(parseFloat(Math.round(resp.total * 100) / 100).toFixed(2)) + " USD");
        $("#subtotal-" + productId).text(String(parseFloat(Math.round(resp.subtotal * 100) / 100).toFixed(2)) + " USD");
    },

    handleChangeProductQuantityError: function () {
        app.utils.toastMessage("Failed to change quantity.");
    },

    handleRemoveLineItemSuccess: function (response, productId) {
        var resp = JSON.parse(response);
        $("#total-total").text(String(parseFloat(Math.round(resp.total * 100) / 100).toFixed(2)) + " USD");
        $('#line-item-' + productId).remove();
        app.utils.toastMessage("Item removed from cart.");
    },

    handleRemoveLineItemError: function () {
        app.utils.toastMessage("Item could not be removed from cart.");
    }

};