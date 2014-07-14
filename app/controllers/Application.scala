package controllers

import _root_.data.ReviewsTable
import play.api._
import play.api.mvc._
import model.{UserId, ContentId, Review}
import org.joda.time.DateTime
import scala.concurrent.Future

object Application extends Controller {

  def submitReview(contentId: String) = Action.async{
    request =>
      val reviewOpt = request.body.asFormUrlEncoded flatMap { form =>
        val formData = form map {case (k, v) => (k, v.head)}
        Review(contentId, formData)
      }
      Future{
        reviewOpt map {r =>
          //record review
          Ok
        } getOrElse BadRequest
      }
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