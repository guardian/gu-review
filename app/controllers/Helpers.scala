package controllers

import data.{Statistics, Persistence}
import model._
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

object Helpers extends Controller {
  def insertFixture() = Action.async {
    Persistence.reviews.record(Review(
      ContentId("books/2014/jul/13/empty-mansions-review-bill-dedman-huguette-clark"),
      UserId("123456"),
      model.Indifferent,
      Some(Comment("Comment")),
      DateTime.now,
      rating = 1
    )).map(Function.const(Ok("Inserted")))
  }

  def testStatsTemplate() = Action {
    Ok(views.html.sentiment(Statistics(Map(
      HatedIt -> 1,
      Indifferent -> 10,
      LovedIt -> 4
    ))))
  }
}
