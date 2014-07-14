package data

import DynamoDB._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._
import model.{Review, ContentId}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Success => ValidationSuccess, Failure => ValidationFailure, ValidationNel}
import scalaz.syntax.traverse._
import scalaz.std.list._

case class ReviewsTable(client: AmazonDynamoDBAsyncClient, tableName: String)(implicit executionContext: ExecutionContext) {
  def get(id: ContentId, limit: Int): Future[List[Review]] = {
    val hashKeyCondition = new Condition()
      .withComparisonOperator(ComparisonOperator.EQ)
      .withAttributeValueList(new AttributeValue().withS(id.get))

    val query = new QueryRequest()
      .withTableName(tableName)
      .withKeyConditions(Map(
        "id" -> hashKeyCondition
      ))
      .withScanIndexForward(false)
      .withLimit(limit)

    client.queryFuture(query) map { response =>
      response.getItems
        .map(DynamoDB.read[Review])
        .toList
        .sequence[({type lambda[A] = ValidationNel[String, A]})#lambda, Review] match {
        case ValidationSuccess(a) => a
        case ValidationFailure(errors) => throw new RuntimeException(errors.toList mkString "\n")
      }
    }
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
