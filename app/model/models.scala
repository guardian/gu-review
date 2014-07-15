package model

import com.amazonaws.services.dynamodbv2.model.{AttributeValue => DynamoDbAttributeValue}
import data.{DynamoDBWrites, DynamoDBReads}
import data.DynamoDB.AttributeValue
import org.joda.time.DateTime
import play.api.libs.json.{JsString, JsValue, Writes}

import scalaz.{Success => ValidationSuccess, Failure => ValidationFailure, NonEmptyList, ValidationNel}

case class Comment(get: String) extends AnyVal
case class ContentId(get: String) extends AnyVal
case class UserId(get: String) extends AnyVal

object Sentiment {
  def stringify(s: Sentiment) = s match {
    case Bored => "bored"
    case Enthralled => "enthralled"
  }

  def fromString(s: String): ValidationNel[String, Sentiment] = s match {
    case "bored" => ValidationSuccess(Bored)
    case "enthralled" => ValidationSuccess(Enthralled)
    case _ => ValidationFailure(NonEmptyList(s"$s is not a valid sentiment!"))
  }

  def apply(s: String): Sentiment = fromString(s) getOrElse (throw new RuntimeException("Invalid Sentiment: "+s))
}

sealed trait Sentiment

case object Bored extends Sentiment
case object Enthralled extends Sentiment

object Review {
  def apply(contentId: String, data: Map[String, String] ): Option[Review] = {
    for {
      author <- data get "userId" map UserId
      sentiment <- data get "sentiment" map {Sentiment(_)}
    } yield Review(
      parent = ContentId(contentId),
      author = author,
      sentiment = sentiment,
      comment = data.get("comment") map Comment,
      createdAt = DateTime.now(),
      0
    )
  }

  implicit val dynamoDbReads = new DynamoDBReads[Review] {
    override def fromAttributeValues(model: Map[String, DynamoDbAttributeValue]): ValidationNel[String, Review] = {
      import AttributeValue._

      for {
        parent <- validateS("parent")(model)
        rating <- validateInt("rating")(model)
        author <- validateS("author")(model)
        comment <- validateOptionString("comment")(model)
        sentimentString <- validateS("sentiment")(model)
        sentiment <- Sentiment.fromString(sentimentString)
        createdAt <- validateDateTime("createdAt")(model)
      } yield Review(
        ContentId(parent),
        UserId(author),
        sentiment,
        comment.map(Comment.apply),
        createdAt,
        rating
      )
    }
  }

  implicit val dynamoDbWrites = new DynamoDBWrites[Review] {
    override def toAttributeValues(a: Review): Map[String, DynamoDbAttributeValue] = Map(
      "parent" -> AttributeValue.s(a.parent.get),
      "rating" -> AttributeValue.n(a.rating),
      "author" -> AttributeValue.s(a.author.get),
      "sentiment" -> AttributeValue.s(Sentiment.stringify(a.sentiment)),
      "createdAt" -> AttributeValue.n(a.createdAt.getMillis)
    ) ++ a.comment.map(comment => Map("comment" -> AttributeValue.s(comment.get))).getOrElse(Map.empty)
  }
}

case class Review(
  parent: ContentId,
  author: UserId,
  sentiment: Sentiment,
  comment: Option[Comment],
  createdAt: DateTime,
  rating: Int
)
