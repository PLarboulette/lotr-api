package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class AdminController@Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {


  def clearKeys () = Action.async {
    Future(Ok("ok"))
  }



}
