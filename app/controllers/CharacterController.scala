package controllers

import javax.inject._

import actors._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.Character
import models.Character.{CreateInput, UpdateInput}
import org.mongodb.scala.Completed
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

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
          (characterActor ? UpdateMessage(updateInput)).mapTo[Try[Character]].map {
            character =>
              Ok(s"Hello ${character.map(_.name).getOrElse("No name")}")
          }
      }.recoverTotal{
        e => Future.successful(BadRequest(s"s${JsError.toJson(e)}"))
      }
  }
}