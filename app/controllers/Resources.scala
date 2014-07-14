package controllers

import play.api._
import play.api.mvc._


object Resources extends Controller {

  def initJs = Action { implicit request =>
    Ok(views.js.assets.init(domain))
  }

  def updateJs = Action { implicit request =>
    Ok(views.js.assets.update(domain))
  }

  def domain(implicit request: RequestHeader): String = s"//${request.host}"
}
