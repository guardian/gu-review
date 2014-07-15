package controllers

import play.api._
import play.api.mvc._
import play.twirl.api.{JavaScript, Html}
import useful.Domain


object Resources extends Controller with Domain {

  def initJs = Action { implicit request =>
    Ok(views.js.assets.init(JavaScript(domain)))
  }

  def updateJs = Action { implicit request =>
    Ok(views.js.assets.update(JavaScript(domain)))
  }
}
