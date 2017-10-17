package controllers

import javax.inject._

import actors._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.Character
import models.Character.{CreateInput, DeleteInput, UpdateInput}
import org.mongodb.scala.Completed
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CharacterController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc){

  val characterActor: ActorRef = actorSystem.actorOf(Props[CharacterActor])

  implicit val timeout : Timeout = 5.seconds

  def find : Action[AnyContent] = Action.async {
    (characterActor ? FindMessage()).mapTo[List[Character]].map(characters => Ok(s"$characters"))
  }

  def findById(id : String): Action[AnyContent] = Action.async {
    (characterActor ? FindByIdMessage(id)).mapTo[Option[Character]].map(character => Ok(s"$character"))
  }

  def create(): Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body.validate[CreateInput].map{
        case (createInput) =>
          (characterActor ? CreateMessage(createInput)).mapTo[Completed].map {
            character =>
              println(character)
              Ok(s"Hello ${character.toString}")
          }

      }.recoverTotal{
        e => Future.successful(BadRequest(s"${JsError.toJson(e)}"))
      }
  }

  def update (): Action[JsValue] = Action.async(parse.json) {
    request =>
      request.body.validate[UpdateInput].map {
        case (updateInput) =>
          (characterActor ? UpdateMessage(updateInput)).mapTo[Either[String, Boolean]].map {
            character =>
              if (character.isRight) {
                Ok(s"${character.right.get}")
              } else {
                Ok(s"${character.left.get}")
              }
          }
      }.recoverTotal{
        e => Future.successful(BadRequest(s"s${JsError.toJson(e)}"))
      }
  }

  def delete(id : String): Action[AnyContent] = Action.async {
    (characterActor ? DeleteMessage(DeleteInput(id))).mapTo[Boolean].map {
      character =>
        Ok(s"$character")
    }
  }


}