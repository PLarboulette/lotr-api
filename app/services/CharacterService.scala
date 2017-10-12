package services

import models.Character

import scala.concurrent.{ExecutionContext, Future}

object CharacterService {


  def getAll (limit : Option[Int] = None) (implicit ec : ExecutionContext) : Future[List[Character]] = {
    println("Here")
    Future.successful(List.empty)
  }

  def getById (id : String) (implicit ec : ExecutionContext) : Future[Option[Character]] = {
    Future.successful(None)
  }

}
