package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import models.Character

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Try}
import akka.pattern.pipe
import akka.util.Timeout

import scala.concurrent.duration._
import services.CharacterService

object CharacterActor {

  // Get messages
  case class GetAll (limit : Option[Int] = None)
  case class GetById(id : String)

  // Create messages
  case class Create (name : String)

  // Update messages 
  case class Update (id : Int, name : Option[String])

}

// ------------------ Entry Point ------------------ //
class CharacterActor extends Actor with ActorLogging {

  import CharacterActor._
  import context._

  val getActor: ActorRef = context.actorOf(Props[GetActor], "GetActor")
  val createActor : ActorRef = context.actorOf(Props[CreateActor], "CreateActor")
//  val updateActor : ActorRef = context.actorOf()

  implicit val timeout : Timeout = 5.seconds

  override def receive: PartialFunction[Any, Unit] = {

    case GetAll(limit) =>
      (getActor ? GetAll(limit)).mapTo[List[Character]] pipeTo sender

    case Create(name) =>
      (createActor ? Create(name)).mapTo[Try[Character]] pipeTo sender

    case Update(id , name) =>
      println("Update")
  }
}

// ------------------ Get Actor ------------------ //

class GetActor extends Actor with ActorLogging {

  import CharacterActor._
  import context._

  val ec: ExecutionContextExecutor = context.dispatcher

  override def receive = {

    case GetAll(limit) =>
      CharacterService.getAll(limit) pipeTo sender

    case GetById(id) =>
      CharacterService.getById(id) pipeTo sender

  }
}

// ------------------ Create Actor ------------------ //

class CreateActor extends Actor with ActorLogging {

  import CharacterActor._
  import context._

  def create(name : String) : Future[Try[Character]] = {
    println(name)
    Future.successful(Failure(new Exception("TODO")))
  }

  override def receive = {
    case Create(name) =>
      create(name) pipeTo sender
  }
}

// ------------------ Update Actor ------------------ //




