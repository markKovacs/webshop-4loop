
var app = app || {};

app.init = function() {

    app.productLogic.addToCartListener();

};

$(document).ready(app.init());
