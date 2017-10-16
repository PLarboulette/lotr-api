package services

import java.util.UUID

import models.{Character, CreateInput, DeleteInput, UpdateInput}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object CharacterService {

  val data = List(
    Character (
      UUID.randomUUID().toString,
      "Elrond",
      None,
      List.empty,
      List.empty
    )
  )

  def getAll () (implicit ec : ExecutionContext) : Future[List[Character]] = {
    Future.successful(data)
  }

  def getById (id : String) (implicit ec : ExecutionContext) : Future[Option[Character]] = {
    Future.successful(data.find(_.id == id))
  }

  def create (createInput : CreateInput) (implicit ec : ExecutionContext) : Future[Try[Character]] = {
    println(s"CreateInput : $createInput")
    Future.successful(Failure(new Exception("To do")))
  }

  def update (updateInput : UpdateInput) (implicit ec : ExecutionContext) : Future[Try[Character]] = {
    println(s"UpdateInput : $updateInput")
    Future.successful(Failure(new Exception("To do")))
  }

  def delete (deleteInput : DeleteInput) (implicit ec : ExecutionContext) : Future[Try[Character]] = {
    println(s"DeleteInput :$deleteInput")
    Future.successful(Failure(new Exception("To do")))
  }

}
