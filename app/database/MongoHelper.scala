package database

import models.Core.{CreateInputTrait, DeleteInputTrait, UpdateInputTrait}
import org.mongodb.scala.{Completed, MongoClient, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

trait MongoHelper[T] {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017/")
  val database: MongoDatabase = mongoClient.getDatabase("lotr")

  def find ()(implicit ec: ExecutionContext): Future[List[T]]

  def findById(id : String)(implicit ec: ExecutionContext): Future[Option[T]]

  def create(createInputTrait: CreateInputTrait)(implicit ec: ExecutionContext): Future[Completed]

  def update(updateInputTrait : UpdateInputTrait)(implicit ec: ExecutionContext): Future[Either[String, Boolean]]

  def delete (deleteInputTrait : DeleteInputTrait) (implicit ec : ExecutionContext) : Future[Boolean]

  def updateAndFind(updateInputTrait: UpdateInputTrait)(implicit ec: ExecutionContext) : Future[Option[T]]
}