package data

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._
import org.joda.time.DateTime

import scala.collection.JavaConversions._
import scala.concurrent.Promise
import scala.util.{Failure, Success}
import scalaz.{Success => ValidationSuccess, Failure => ValidationFailure, NonEmptyList, ValidationNel, Validation}

trait DynamoDBWrites[A] {
  def toAttributeValues(a: A): Map[String, AttributeValue]
}

trait DynamoDBReads[A] {
  def fromAttributeValues(attributeValues: Map[String, AttributeValue]): ValidationNel[String, A]
}

object DynamoDB {
  def write[A](a: A)(implicit writes: DynamoDBWrites[A]) =
    writes.toAttributeValues(a)

  def read[A](attributeValues: java.util.Map[String, AttributeValue])(implicit reads: DynamoDBReads[A]): ValidationNel[String, A] =
    reads.fromAttributeValues(attributeValues.toMap)

  object AttributeValue {
    def s(s: String) = new AttributeValue().withS(s)
    def n(n: Int) = new AttributeValue().withN(n.toString)
    def n(n: Long) = new AttributeValue().withN(n.toString)

    def validateAttributeValue(name: String)(attributeValues: Map[String, AttributeValue]): ValidationNel[String, AttributeValue] =
      attributeValues.get(name).map(ValidationSuccess.apply)
        .getOrElse(ValidationFailure(NonEmptyList(s"$name not in attribute values")))

    private def tryOr[A](errorMessage: String)(block: => A): ValidationNel[String, A] =
      try {
        ValidationSuccess(block)
      } catch {
        case _: Throwable => ValidationFailure(NonEmptyList(errorMessage))
      }

    def validateS(name: String)(attributeValues: Map[String, AttributeValue]): ValidationNel[String, String] =
      validateAttributeValue(name)(attributeValues) flatMap { s =>
        tryOr(s"$name was not a String")(s.getS)
      }

    def validateOptionString(name: String)(attributeValues: Map[String, AttributeValue]): ValidationNel[String, Option[String]] =
      attributeValues.get(name) map { s =>
        tryOr(s"$name was not a String")(Some(s.getS))
      } getOrElse {
        ValidationSuccess(None)
      }

    def validateLong(name: String)(attributeValues: Map[String, AttributeValue]): ValidationNel[String, Long] =
      validateAttributeValue(name)(attributeValues) flatMap { s =>
        tryOr(s"$name was not a Long")(s.getN.toLong)
      }

    def validateInt(name: String)(attributeValues: Map[String, AttributeValue]): ValidationNel[String, Int] =
      validateAttributeValue(name)(attributeValues) flatMap { s =>
        tryOr(s"$name was not an Int")(s.getN.toInt)
      }

    def validateDateTime(name: String)(attributeValues: Map[String, AttributeValue]): ValidationNel[String, DateTime] =
      validateAttributeValue(name)(attributeValues) flatMap { s =>
        tryOr(s"$name was not a DateTime")(new DateTime(s.getN.toLong))
      }
  }

  implicit class RichDynamoDBAsyncClient(client: AmazonDynamoDBAsyncClient) {
    private def createHandler[A <: com.amazonaws.AmazonWebServiceRequest, B]() = {
      val promise = Promise[B]()

      val handler = new AsyncHandler[A, B] {
        override def onSuccess(request: A, result: B): Unit = promise.complete(Success(result))

        override def onError(exception: Exception): Unit = promise.complete(Failure(exception))
      }

      (promise.future, handler)
    }

    private def asFuture[A <: com.amazonaws.AmazonWebServiceRequest, B](f: AsyncHandler[A, B] => Any) = {
      val (future, handler) = createHandler[A, B]()
      f(handler)
      future
    }

    def batchGetItemFuture(request: BatchGetItemRequest) =
      asFuture[BatchGetItemRequest, BatchGetItemResult](client.batchGetItemAsync(request, _))

    def batchWriteItemFuture(request: BatchWriteItemRequest) = {
      asFuture[BatchWriteItemRequest, BatchWriteItemResult](client.batchWriteItemAsync(request, _))
    }

    def scanFuture(request: ScanRequest) =
      asFuture[ScanRequest, ScanResult](client.scanAsync(request, _))

    def deleteItemFuture(request: DeleteItemRequest) =
      asFuture[DeleteItemRequest, DeleteItemResult](client.deleteItemAsync(request, _))

    def queryFuture(request: QueryRequest) =
      asFuture[QueryRequest, QueryResult](client.queryAsync(request, _))
  }
}
