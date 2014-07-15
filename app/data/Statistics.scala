package data

import model.{Review, Sentiment}

import scalaz.std.map._

object Statistics {
  type SentimentCounts = Map[Sentiment, Int]

  def apply(reviews: List[Review]): SentimentCounts = {
    reviews.foldLeft(Map.empty[Sentiment, Int]) { (s: SentimentCounts, a: Review) =>
      insertWith(s, a.sentiment, 1)(_ + _)
    }
  }
}
