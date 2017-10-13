package actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import models.{Character, CreateInput, GetAll, GetById, UpdateInput}
import services.CharacterService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Try}

case class CreateMessage (createInput : CreateInput)
case class UpdateMessage (updateInput : UpdateInput)

// ------------------ Entry Point ------------------ //
class CharacterActor extends Actor with ActorLogging {

  import context._

//  val getActor: ActorRef = context.actorOf(Props[GetActor], s"GetActor_${UUID.randomUUID()}")
  val createActor : ActorRef = context.actorOf(Props[CreateActor], s"CreateActor_${UUID.randomUUID()}")
//  val updateActor : ActorRef = context.actorOf()

  implicit val timeout : Timeout = 5.seconds

  override def preStart(): Unit = {
    println(s"CharacterActor : ${self.path} started !")
  }

  override def postStop(): Unit = {
    println("CharacterActor killed !")
  }

  override def receive: PartialFunction[Any, Unit] = {

    case GetAll() =>
      val getActor: ActorRef = context.actorOf(Props[GetActor], s"GetActor_${UUID.randomUUID()}")
      (getActor ? GetAll()).mapTo[List[Character]] pipeTo sender
      context.stop(self)

    case CreateMessage(createInput) =>
      (createActor ? CreateMessage(createInput)).mapTo[Try[Character]] pipeTo sender
      context.stop(self)

    case UpdateMessage(updateInput) =>
      println("Update")
  }
}

// ------------------ Get Actor ------------------ //

class GetActor extends Actor with ActorLogging {

  import context._

  val ec: ExecutionContextExecutor = context.dispatcher

  override def preStart(): Unit = {
    // push dans du rabbit ?
//    println(self.path)
  }

  override def postStop(): Unit = {
//    println("GetActor killed")
  }


  override def receive = {

    case GetAll() =>
      CharacterService.getAll() pipeTo sender
      context.stop(self)

    case GetById(id) =>
      CharacterService.getById(id) pipeTo sender
      context.stop(self)

  }
}

// ------------------ Create Actor ------------------ //

class CreateActor extends Actor with ActorLogging {

  import context._

  def create(createInput : CreateInput) : Future[Try[Character]] = {
    println(createInput.name)
    Future.successful(Failure(new Exception("TODO")))
  }

  override def receive = {
    case CreateMessage(createInput) =>
      create(createInput) pipeTo sender
  }
}

// ------------------ Update Actor ------------------ //




