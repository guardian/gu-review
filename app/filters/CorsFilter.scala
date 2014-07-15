package filters

import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CorsFilter extends Filter {
  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    f(rh).map(_.withHeaders("Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS, PUT, DELETE",
      "Access-Control-Allow-Headers" -> "x-requested-with,content-type,Cache-Control,Pragma,Date"
    ))
  }
}