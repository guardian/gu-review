@(domain: JavaScript)

require(["bonzo", "qwery", "bean", "reqwest"], function(bonzo, qwery, bean, reqwest){
    var doReview = function(sentiment, comment){
        var reviewDataUrl = "@domain/reviews" + document.location.pathname + "?callback=?",
            data;
        if (typeof comment === 'undefined') {
            data = {
                sentiment: sentiment
            };
        } else {
            data = {
                sentiment: sentiment,
                comment: comment
            };
        }

        reqwest({
            url: reviewDataUrl,
            method: 'post',
            data: data,
            success: function(response) {
                // insert stats and vote controls
                $(".discussion__comments__container").html(response.html);
            }
        });
    }
});
