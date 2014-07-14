package data

import DynamoDB._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import model.{Review, ContentId}

case class ReviewsTable(client: AmazonDynamoDBAsyncClient) {
  def get(id: ContentId): Seq[Review] = ???

  def record(review: Review): Unit = ???
}
