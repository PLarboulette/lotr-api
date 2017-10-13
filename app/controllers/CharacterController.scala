package controllers

import java.util.UUID
import javax.inject._

import actors.{CharacterActor, CreateMessage}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.{Character, CreateInput, GetAll, GetById}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.Try

@Singleton
class CharacterController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc){


  implicit val timeout : Timeout = 5.seconds


  def getAll : Action[AnyContent] = Action.async {
    val characterActor: ActorRef = actorSystem.actorOf(Props[CharacterActor], s"CharacterActor_${UUID.randomUUID()}")
    (characterActor ? GetAll()).mapTo[List[Character]].map {
      characters =>
        Ok(s"${characters.size}")
    }
  }

  def getById(id : String): Action[AnyContent] = Action.async {

    val characterActor: ActorRef = actorSystem.actorOf(Props[CharacterActor], s"CharacterActor_${UUID.randomUUID()}")
    (characterActor ? GetById(id)).mapTo[List[Character]].map {
      characters =>
        Ok(s"${characters.size}")
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body.validate[CreateInput].map{
        case (createInput) =>
          val characterActor: ActorRef = actorSystem.actorOf(Props[CharacterActor], s"CharacterActor_${UUID.randomUUID()}")
          (characterActor ? CreateMessage(createInput)).mapTo[Try[Character]].map {
            character =>
              Ok(s"Hello ${character.map(_.name).getOrElse("No name")}")
          }

      }.recoverTotal{
        e => Future.successful(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
  }



}
