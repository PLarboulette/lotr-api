package services

import java.util.UUID

import database.MongoHelper
import models.Character.{CreateInput, DeleteInput, UpdateInput}
import models.Core.{CreateInputTrait, UpdateInputTrait}
import models.{Character, Core}
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Completed, Document, MongoCollection}

import scala.concurrent.{ExecutionContext, Future}

object CharacterService extends MongoHelper[Character] {

  val COLLECTION = "characters"
  val coll : MongoCollection[Document] = database.getCollection(COLLECTION)

  val data = List(
    Character (
      "Test",
      "Elrond",
      4356,
      List.empty,
      List.empty
    )
  )

  override def find()(implicit ec: ExecutionContext): Future[List[Character]] = {
    Future.successful(data)
  }

  override def findById (id : String) (implicit ec : ExecutionContext) : Future[Option[Character]] = {
    Future.successful(data.find(_.id == id))
  }

  /*document.get("_id").get.asString().getValue,
    document.get("name").map(_.asString().getValue).head,
    document.get("side").map { elem => Side.values.filter(_.id == elem.asInt32().getValue).head},
    document.get("friends").get.asArray().toArray.toList.map(_.asInstanceOf[BsonValue].asString().getValue)*/

  override def create(createInputTrait : CreateInputTrait )(implicit ec : ExecutionContext) : Future[Completed] = {

    val createInput = createInputTrait.asInstanceOf[CreateInput]

    val character = Character(UUID.randomUUID().toString, createInput.name, createInput.age.getOrElse(-1),
      createInput.ancestors.getOrElse(List.empty), createInput.descendants.getOrElse(List.empty))

    for {
      insertedElement <- coll.insertOne(character).toFuture().recoverWith {case e : Throwable => Future.failed(e)}
    } yield {
      insertedElement
    }
  }

  override def update(updateInputTrait: UpdateInputTrait)(implicit ec: ExecutionContext): Future[Either[String, BsonValue]] = {

    val updateInput = updateInputTrait.asInstanceOf[UpdateInput]
    for {
      oldDocument <- coll.find(Document("_id" -> updateInput.id)).toFuture().recoverWith{case e : Throwable => Future.failed(e)}
      oldCharacterOpt = oldDocument.headOption.map(_.asInstanceOf[Character])
      updatedCharacter <-
      if (oldCharacterOpt.isDefined) {
        val oldCharacter = oldCharacterOpt.get
        val newCharacter = oldCharacter.copy(
          name = updateInput.name.getOrElse(oldCharacter.name),
          age = updateInput.age.getOrElse(oldCharacter.age),
          ancestors = updateInput.ancestors.getOrElse(oldCharacter.ancestors),
          descendants = updateInput.descendants.getOrElse(oldCharacter.descendants)
        )
        coll.updateOne(equal("_id", updateInput.id), newCharacter).toFuture().recoverWith {
          case e : Throwable => Future.failed(e)
        }.map(_.getUpsertedId).map(Right(_))
      } else {
        Future.successful(Left(s"Character with _id ${updateInput.id} is not defined"))
      }
    } yield {
      updatedCharacter
    }

  }

  override def delete(deleteInputTrait: Core.DeleteInputTrait)(implicit ec: ExecutionContext): Future[Boolean] = {

    val deleteInput = deleteInputTrait.asInstanceOf[DeleteInput]
    for {
      deletedItem <- coll.deleteMany(equal("_id", deleteInput.id)).toFuture()
        .recoverWith {
          case e: Throwable =>
            println(e.getMessage)
            Future.failed(e)
        }
        .map(result => if (result.getDeletedCount == 1) true else false)

    } yield {
      deletedItem
    }
  }

}
