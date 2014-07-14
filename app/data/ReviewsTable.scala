package data

import DynamoDB._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._
import model.{UserId, Review, ContentId}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Success => ValidationSuccess, Failure => ValidationFailure, Validation, ValidationNel}
import scalaz.syntax.traverse._
import scalaz.std.list._

case class ReviewsTable(client: AmazonDynamoDBAsyncClient, tableName: String)(implicit executionContext: ExecutionContext) {
  def reviewKey(contentId: ContentId, authorId: UserId) =
    Map(
      "parent" -> new AttributeValue().withS(contentId.get),
      "author" -> new AttributeValue().withS(authorId.get)
    )

  implicit class RichValidation[E, A](validation: ValidationNel[E, A]) {
    def getOrDie = validation match {
      case ValidationSuccess(a) => a
      case ValidationFailure(errors) => throw new RuntimeException(errors.toList mkString "\n")
    }
  }

  def get(id: ContentId, limit: Int): Future[List[Review]] = {
    val query = new QueryRequest()
      .withTableName(tableName)
      .withKeyConditions(Map(
        "parent" -> new Condition()
          .withComparisonOperator(ComparisonOperator.EQ)
          .withAttributeValueList(new AttributeValue().withS(id.get))
      ))
      .withScanIndexForward(false)
      .withLimit(limit)

    client.queryFuture(query) map { response =>
      response.getItems
        .map(DynamoDB.read[Review])
        .toList
        .sequence[({type λ[A] = ValidationNel[String, A]})#λ, Review]
        .getOrDie
    }
  }

  def upVote(contentId: ContentId, authorId: UserId): Future[Unit] = {
    val request = new UpdateItemRequest()
      .withKey(reviewKey(contentId, authorId))
      .withAttributeUpdates(Map(
        "rating" -> new AttributeValueUpdate()
          .withAction(AttributeAction.ADD)
          .withValue(new AttributeValue().withN("1"))
      ))

    client.updateItemFuture(request).map(Function.const(()))
  }

  def downVote(contentId: ContentId, authorId: UserId): Future[Unit] = {
    val request = new UpdateItemRequest()
      .withKey(reviewKey(contentId, authorId))
      .withAttributeUpdates(Map(
      "rating" -> new AttributeValueUpdate()
        .withAction(AttributeAction.ADD)
        .withValue(new AttributeValue().withN("-1"))
    ))

    client.updateItemFuture(request).map(Function.const(()))
  }

  def record(review: Review): Future[Unit] = {
    val requestItems = Map(
      tableName -> List(
        new WriteRequest().withPutRequest(
          new PutRequest().withItem(DynamoDB.write(review))
        )
      ).asJava
    ).asJava

    val writeRequest = new BatchWriteItemRequest()
      .withRequestItems(requestItems)

    client.batchWriteItemFuture(writeRequest).map(Function.const(()))
  }
}
