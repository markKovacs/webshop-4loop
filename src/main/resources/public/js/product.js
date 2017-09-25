
var app = app || {};

app.productLogic = {

    // Only for example, to be deleted.
    boardDetailsListener: function () {
        $("#boards").on("click", ".details", function(ev) {
            ev.stopPropagation();
            $('.success').remove();
            $('.error').remove();
            var boardId = $(this).data("board-id");
            app.dataHandler.getBoardDetails(boardId);
        });
    },

    testFunction: function () {
        console.log("Called via product.js testFunction()");
    }

};