require(["bonzo", "qwery", "bean"], function(bonzo, qwery){
    function $(selector, context) {
        return bonzo(qwery(selector, context));
    }

    function isReviewPage() {
        return guardian.config.page.tones == "Reviews" && guardian.config.page.section == "books";
    }

    if (isReviewPage()) {

    }
});
