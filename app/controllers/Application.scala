package controllers

import _root_.data.{Persistence, Statistics}
import play.api._
import play.api.libs.Jsonp
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import play.api.mvc._
import model.{UserId, ContentId}
import play.twirl.api.Html
import useful.Domain
import model.Review
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ReviewsResponse {
  implicit val htmlWrites = new Writes[Html] {
    override def writes(o: Html): JsValue = JsString(o.body)
  }

  implicit val jsonWrites = Json.writes[ReviewsResponse]
}

case class ReviewsResponse(
  reviews: Html,
  statistics: Html
)

object Application extends Controller with Domain {
  private val reviewsTable = Persistence.reviews

  def submitReview(contentId: String) = Action.async { implicit request =>
    val reviewOpt = request.body.asFormUrlEncoded flatMap { form =>
      val formData: Map[String, String] = form map {case (k, v) => (k, v.head)}
      Review(contentId, formData)
    }

    reviewOpt map {r =>
      reviewsTable.record(r) map {_ => Ok("")}
      } getOrElse Future.successful(BadRequest(""))
  }

  def upVote(contentId: String, userId: String) = Action.async {
    Persistence.reviews.upVote(ContentId(contentId), UserId(userId)).map(Function.const(Ok("Upvoted!")))
  }

  def downVote(contentId: String, userId: String) = Action.async {
    Persistence.reviews.downVote(ContentId(contentId), UserId(userId)).map(Function.const(Ok("Downvoted!")))
  }

  def displayReviews(contentId: String) = Action.async { implicit request =>
    Persistence.reviews.get(ContentId(contentId), 100) map { reviews =>
      Ok(Jsonp(request.queryString.get("callback").flatMap(_.headOption).getOrElse("callback"), Json.toJson(ReviewsResponse(
        views.html.reviews(reviews, domain),
        views.html.sentiment(Statistics.fromReviews(reviews), domain)
      ))))
    }
  }

  def preflight(url: String) = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Allow" -> "*",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent")
  }
}
