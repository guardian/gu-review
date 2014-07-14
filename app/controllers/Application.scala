package controllers

import _root_.data.Persistence
import play.api._
import play.api.mvc._
import model.ContentId

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def submitReview(contentId: String) = Action {
    Ok("")
  }

  def upVote(reviewId: String) = vote(reviewId, isUpVote = true)
  def downVote(reviewId: String) = vote(reviewId, isUpVote = false)

  def vote(reviewId: String, isUpVote: Boolean) = Action {
    Ok("")
  }

  def displayReviews(contentId: String) = Action.async {
    Persistence.reviews.get(ContentId(contentId), 100) map { reviews =>
      Ok(views.html.reviews(reviews))
    }
  }
}
