
var app = app || {};

app.init = function() {

    app.productLogic.addToCartListener();
    app.productLogic.reviewQuantityChangeListener();

};

$(document).ready(app.init());
