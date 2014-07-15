package controllers

import _root_.data.Persistence
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

  def displayReviews(contentId: String) = Action.async { implicit request =>
    // should be:
    // {
    //   "html" -> views.html.reviews(reviews, domain)
    //   "stats" -> JSON of the stats for the sentiment
    // }

    Persistence.reviews.get(ContentId(contentId), 100) map { reviews =>
      Ok(views.html.reviews(reviews, domain))
    }
  }
}
