package controllers

import data.{Statistics, Persistence}
import model._
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}
import Persistence.reviews.record

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Helpers extends Controller {
  def insertFixture() = Action.async {
    val contentId = ContentId("books/2014/jul/13/empty-mansions-review-bill-dedman-huguette-clark")

    Future.sequence(Seq(
      record(Review(
        contentId,
        UserId("adamnfish"),
        LovedIt,
        Some(Comment("This was wonderful. It brought tears to my eyes. It made me rue the day I decided to become " +
          "a computer programmer - if only I could be a reclusive heiress!")),
        DateTime.now,
        rating = -12
      )),
      record(Review(
        contentId,
        UserId("robertchristgau"),
        HatedIt,
        Some(Comment("Ersatz shit")),
        DateTime.now.minusHours(2).minusMinutes(20),
        rating = 20
      )),
      record(Review(
        contentId,
        UserId("franciscarr"),
        Indifferent,
        None,
        DateTime.now.minusHours(1).minusMinutes(3).minusSeconds(45),
        rating = 0
      ))
    )).map(Function.const(Ok("Inserted")))
  }

  def testStatsTemplate() = Action {
    Ok(views.html.sentiment(Statistics(Map(
      HatedIt -> 1,
      Indifferent -> 10,
      LovedIt -> 4
    )), "//"))
  }
}
