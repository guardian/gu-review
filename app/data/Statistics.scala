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

case class Statistics(counts: Map[Sentiment, Int])