@(domain: JavaScript)

require(["bonzo", "qwery", "bean", "reqwest"], function(bonzo, qwery, bean, reqwest){
    var $ = function(selector, context) {
            return bonzo(qwery(selector, context));
        },
        isReviewPage = function() {
            return guardian.config.page.tones == "Reviews" && guardian.config.page.section == "books";
        },
        initialLoad = function() {
            if (isReviewPage()) {
                var reviewDataUrl = "@domain/reviews" + document.location.pathname;
                reqwest({
                    url: reviewDataUrl,
                    method: 'get',
                    type: "jsonp",
                    success: function(response) {
                        // insert stats and vote controls
                        $(".discussion__comments__container").html(response.reviews);
                        $(".tone-background").append(bonzo(bonzo.create(response.statistics)));
                    }
                });
            }
        };

    console.log("blah", isReviewPage());
    initialLoad();
});
