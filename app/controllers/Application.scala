package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def submitReview(contentId: String) = Action {
    Ok()
  }

  def upVote(reviewId: String) = vote(reviewId, isUpVote = true)
  def downVote(reviewId: String) = vote(reviewId, isUpVote = false)

  def vote(reviewId: String, isUpVote: Boolean) = Action {
    Ok()
  }

  def displayReviews(contentId: String) = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}