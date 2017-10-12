package controllers

import javax.inject._

import actors.CharacterActor
import actors.CharacterActor.GetAll
import akka.actor.{ActorSystem, Props}
import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout
import models.Character
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

@Singleton
class CharacterController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc){

  def getAll(): Action[AnyContent] = Action.async {
    implicit val timeout : Timeout = 5.seconds
    val characterActor = actorSystem.actorOf(Props[CharacterActor], "CharacterActor")
    (characterActor ? GetAll(None)).mapTo[List[Character]].map {
      characters =>
        Ok(s"${characters.size}")
    }
  }



}
