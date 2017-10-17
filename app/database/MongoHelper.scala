package database

import models.Core.{CreateInputTrait, DeleteInputTrait, UpdateInputTrait}
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.{Completed, MongoClient, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

trait MongoHelper[T] {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:32768/")
  val database: MongoDatabase = mongoClient.getDatabase("lotr")

  def find ()(implicit ec: ExecutionContext): Future[List[T]]

  def findById(id : String)(implicit ec: ExecutionContext): Future[Option[T]]

  def create(createInputTrait: CreateInputTrait)(implicit ec: ExecutionContext): Future[Completed]

  def update(updateInputTrait : UpdateInputTrait)(implicit ec: ExecutionContext): Future[Either[String, BsonValue]]

  def delete (deleteInputTrait : DeleteInputTrait) (implicit ec : ExecutionContext) : Future[Boolean]
}


/*collection.updateMany(lt("qty", 50),
combine(set("size.uom", "in"), set("status", "P"), currentDate("lastModified"))
).execute()*/



/*
 def updateAndFind (id : String, field : String, value : AnyRef)  (implicit ec : ExecutionContext, collection : String) : Future[Option[T]] = {
   for {
     resultUpdate <- update(id, field, value)
     heroUpdated <- if (resultUpdate) getByID(id) else Future.successful(None)
   } yield {
     heroUpdated
   }
 }



 eetByID(id : String)(implicit ec:ExecutionContext, collection : String): Future[Option[T]] = {
   val coll : MongoCollection[Document] = database.getCollection(collection)
   coll.find(equal("_id",  id)).toFuture().recoverWith {
     case e: Throwable => Future.failed(e)
   }.map(_.headOption.map(convertToT))
 }

 def delete (field : String, value : String) (implicit ec : ExecutionContext, collection : String) : Future[Boolean] = {
   val coll : MongoCollection[Document] = database.getCollection(collection)

 }*/

