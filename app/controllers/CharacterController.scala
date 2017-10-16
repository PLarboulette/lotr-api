package controllers

import javax.inject._

import actors.{CharacterActor, CreateMessage, UpdateMessage}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.{Character, CreateInput, GetAll, GetById, UpdateInput}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class CharacterController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc){

  val characterActor: ActorRef = actorSystem.actorOf(Props[CharacterActor])

  implicit val timeout : Timeout = 5.seconds



  def getAll : Action[AnyContent] = Action.async {
    (characterActor ? GetAll()).mapTo[List[Character]].map {
      characters =>
        Ok(s"${characters.size}")
    }
  }

  def getById(id : String): Action[AnyContent] = Action.async {
    (characterActor ? GetById(id)).mapTo[List[Character]].map {
      characters =>
        Ok(s"${characters.size}")
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body.validate[CreateInput].map{
        case (createInput) =>
          (characterActor ? CreateMessage(createInput)).mapTo[Try[Character]].map {
            character =>
              Ok(s"Hello ${character.map(_.name).getOrElse("No name")}")
          }

      }.recoverTotal{
        e => Future.successful(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
  }

  def update (): Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body.validate[UpdateInput].map {
        case (updateInput) =>
          (characterActor ? UpdateMessage(updateInput)).mapTo[Try[Character]].map {
            character =>
              Ok(s"Hello ${character.map(_.name).getOrElse("No name")}")
          }
      }.recoverTotal{
        e => Future.successful(BadRequest("Detected error:"+ JsError.toJson(e)))
      }

  }



}
