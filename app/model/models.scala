package model

import org.joda.time.DateTime

case class ContentId(get: String) extends AnyVal
case class UserId(get: String) extends AnyVal

sealed trait Sentiment

case object Bored extends Sentiment
case object Enthralled extends Sentiment

case class Review(
  parent: ContentId,
  author: UserId,
  sentiment: Sentiment,
  comment: Option[String],
  createdAt: DateTime
)
