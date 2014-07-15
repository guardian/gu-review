package controllers

import _root_.data.Persistence
import play.api._
import play.api.mvc._
import model.{UserId, ContentId}
import useful.Domain

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller with Domain {

  def submitReview(contentId: String) = Action {
    Ok("")
  }

  def upVote(contentId: String, userId: String) = Action.async {
    Persistence.reviews.upVote(ContentId(contentId), UserId(userId)).map(Function.const(Ok("Upvoted!")))
  }

  def downVote(contentId: String, userId: String) = Action.async {
    Persistence.reviews.downVote(ContentId(contentId), UserId(userId)).map(Function.const(Ok("Downvoted!")))
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
