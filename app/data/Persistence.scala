package data

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import scala.concurrent.ExecutionContext.Implicits.global

object Persistence {
  private val dynamoDbClient = new AmazonDynamoDBAsyncClient()
  dynamoDbClient.setRegion(Region.getRegion(Regions.EU_WEST_1))

  val reviews = ReviewsTable(dynamoDbClient, "guReviews")
}
