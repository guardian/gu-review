@(domain: JavaScript)

require(["bonzo", "qwery", "bean", "reqwest"], function(bonzo, qwery, bean, reqwest){
    var $ = function(selector, context) {
            return bonzo(qwery(selector, context));
        },
        isReviewPage = function() {
            return guardian.config.page.tones == "Reviews" && guardian.config.page.section == "books";
        };
        initialLoad = function() {
            if (isReviewPage()) {
                var reviewDataUrl = "@domain/reviews/" + document.location.pathname;
                reqwest({
                    url: reviewDataUrl,
                    method: 'get',
                    success: function(response) {
                        $(".discussion__comments__container").html(response);
                    }
                });
            }
        };

    initialLoad();
});
