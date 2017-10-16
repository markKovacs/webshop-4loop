
var app = app || {};

app.init = function() {

    app.productLogic.addToCartListener();
    app.productLogic.reviewQuantityChangeListener();
    app.productLogic.reviewRemoveItemListener();

};

$(document).ready(app.init());
