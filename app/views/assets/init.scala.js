@(domain: JavaScript)

require(["bonzo", "qwery", "bean", "reqwest"], function(bonzo, qwery, bean, reqwest){
    var $ = function(selector, context) {
            return bonzo(qwery(selector, context));
        },
        isReviewPage = function() {
            return guardian.config.page.tones == "Reviews" && guardian.config.page.section == "books";
        },
        popupReviewBox = function(sentiment) {
            var reviewBox = $(".review");
            reviewBox.css("display", "block");
            reviewBox.data("sentiment", sentiment);
        },
        hideReviewBox= function() {
            var reviewBox = $(".review");
            reviewBox.css("display", "none");
            reviewBox.data("sentiment", "");
        },
        doReview = function() {
            var sentiment = $(".review").data("sentiment"),
                comment = $("review-comment").val();
            reqwest({
                url: "@domain/review" + document.location.pathname,
                method: "post",
                crossOrigin: true,
                data: {
                    sentiment: sentiment,
                    comment: comment,
                    userId: Math.random().toString(36).substring(7)
                },
                success: function(response) {
                    console.log("done", response);
                }
            });
            hideReviewBox();
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
                        $(".face img").css("cursor", "pointer").each(function(el){
                            bean.on(el, "click", function(e) {
                                var el = bonzo(el);
                                popupReviewBox(el.data("sentiment"));
                            });
                        });
                        bean.on($("#review-form").get(0), "submit", function(e){
                            e.preventDefault();
                            doReview()
                        });
                    }
                });
            }
        };

    console.log("review", isReviewPage());
    initialLoad();
});
