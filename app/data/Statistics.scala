package data

import model.{Review, Sentiment}

import scalaz.std.map._

object Statistics {
  def fromReviews(reviews: List[Review]): Statistics = {
    Statistics(reviews.foldLeft(Map.empty[Sentiment, Int]) { (s: Map[Sentiment, Int], a: Review) =>
      insertWith(s, a.sentiment, 1)(_ + _)
    })
  }
}

case class Statistics(counts: Map[Sentiment, Int]) {
  lazy val total = counts.values.reduce(_ + _)

  def percentage(sentiment: Sentiment) = total match {
    case 0 => 0f
    case otherwise => 100 * (counts.getOrElse(sentiment, 0).toFloat / total)
  }

  def percentageString(sentiment: Sentiment) = "%.2f%%".format(percentage(sentiment))
}