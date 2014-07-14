package controllers

import play.api._
import play.api.mvc._
import model.{Comment, UserId, ContentId, Review}
import org.joda.time.DateTime


object Application extends Controller {

  def submitReview(contentId: String) = Action {
    Ok("")
  }

  def upVote(reviewId: String) = vote(reviewId, isUpVote = true)
  def downVote(reviewId: String) = vote(reviewId, isUpVote = false)

  def vote(reviewId: String, isUpVote: Boolean) = Action {
    Ok("")
  }

  def displayReviews(contentId: String) = Action {
    val reviews: List[Review] = List(
      Review(
        ContentId("/books/2014/jul/13/empty-mansions-review-bill-dedman-huguette-clark"),
        UserId("123456"),
        model.Bored,
        Some(Comment("Comment")),
        DateTime.now,
        rating = 1
      )
    ) // TODO @Nick

    Ok(views.html.reviews(reviews))
  }
}
