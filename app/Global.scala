import data.Persistence
import model._
import org.joda.time.DateTime
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    Persistence.reviews.record(Review(
      ContentId("adams face"),
      UserId("rob"),
      Bored,
      Some(Comment("O___O")),
      DateTime.now,
      999
    ))
  }
}
