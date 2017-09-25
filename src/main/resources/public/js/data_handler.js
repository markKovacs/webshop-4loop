
var app = app || {};

app.dataHandler = {

    // Only for example , to be deleted.
    createNewCard: function (boardId, cardTitle, boardTitle) {
        // Create new card in the given board and save it.
        $.ajax({
            url: '/api/new_card',
            method: 'POST',
            dataType: 'json',
            data: {
                title: cardTitle,
                board_id: boardId
            },
            success: function(response) {
                if (response === 'data_error') {
                    app.cards.flashDataErrorMessage();
                } else {
                    app.cards.insertNewCard(response.id, cardTitle, response.card_order, boardTitle, boardId);
                    app.cards.resetForm(cardTitle);
                }
            },
            error: function() {
                window.location.replace('/login?error=timedout');
            }
        });
    }

};