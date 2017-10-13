package services

import java.util.UUID

import models.Character

import scala.concurrent.{ExecutionContext, Future}

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

}
