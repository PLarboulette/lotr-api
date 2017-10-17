package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import models.Character.{CreateInput, DeleteInput, UpdateInput}
import services.CharacterService

import scala.concurrent.duration._

case class FindMessage()
case class FindByIdMessage(id : String)
case class CreateMessage (createInput : CreateInput)
case class UpdateMessage (updateInput : UpdateInput)
case class DeleteMessage (deleteInput: DeleteInput)

// ------------------ Entry Point ------------------ //

class CharacterActor extends Actor with ActorLogging {

  import context._

  implicit val timeout : Timeout = 5.seconds

  override def preStart(): Unit = {
    println(s"CharacterActor : ${self.path} started !")
  }

  override def postStop(): Unit = {
    println("CharacterActor killed !")
  }

  implicit val logger: Boolean = true

  override def receive: PartialFunction[Any, Unit] = {

    case FindMessage() =>
      CharacterService.find().pipeTo(sender)

    case FindByIdMessage(id) =>
      CharacterService.findById(id).pipeTo(sender)

    case CreateMessage(createInput) =>
      CharacterService.create(createInput).pipeTo(sender)

    case UpdateMessage(updateInput) =>
      CharacterService.update(updateInput).pipeTo(sender)

    case DeleteMessage(deleteInput) =>
      CharacterService.delete(deleteInput).pipeTo(sender)
  }
}