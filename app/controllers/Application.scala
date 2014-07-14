package controllers

import play.api._
import play.api.mvc._
import model.{Comment, UserId, ContentId, Review}
import org.joda.time.DateTime
import useful.Domain


object Application extends Controller with Domain {

  def submitReview(contentId: String) = Action {
    Ok("")
  }

  def upVote(reviewId: String) = vote(reviewId, isUpVote = true)
  def downVote(reviewId: String) = vote(reviewId, isUpVote = false)

  def vote(reviewId: String, isUpVote: Boolean) = Action {
    Ok("")
  }

  def displayReviews(contentId: String) = Action { implicit request =>
    val reviews = List(
      Review(
        ContentId("/books/2014/jul/13/empty-mansions-review-bill-dedman-huguette-clark"),
        UserId("123456"),
        model.Bored,
        Some(Comment("Comment")),
        DateTime.now,
        rating = 1
      )
    ) // TODO @Nick

    // should be:
    // {
    //   "html" -> views.html.reviews(reviews, domain)
    //   "stats" -> JSON of the stats for the sentiment
    // }

    Ok(views.html.reviews(reviews, domain))
  }
}
