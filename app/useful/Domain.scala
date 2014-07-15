package useful

import play.api.mvc.RequestHeader


trait Domain {
  def domain(implicit request: RequestHeader): String = s"//${request.host}"
}
