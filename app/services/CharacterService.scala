package services

import java.util.UUID
import database.MongoHelper
import models.Character.{CreateInput, DeleteInput, UpdateInput}
import models.Core.{CreateInputTrait, UpdateInputTrait}
import models.{Character, Core}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Completed, Document, MongoCollection}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object CharacterService extends MongoHelper[Character] {

  val COLLECTION = "characters"
  val coll : MongoCollection[Document] = database.getCollection(COLLECTION)

  override def find()(implicit ec: ExecutionContext): Future[List[Character]] = {
    Try {
      for {
        documents <- coll.find().toFuture().recoverWith{case e : Throwable => Future.failed(e)}
      } yield {
        documents.toList.map(Character.toCharacter)
      }
    } match {
      case Success(v) => v
      case Failure(e) =>
        println(e.getMessage)
        Future.successful(List.empty)
    }
  }

  override def findById (id : String) (implicit ec : ExecutionContext) : Future[Option[Character]] = {
    //TODO
    Future.successful(None)
  }

  override def create(createInputTrait : CreateInputTrait )(implicit ec : ExecutionContext) : Future[Completed] = {
    val createInput = createInputTrait.asInstanceOf[CreateInput]
    val character = Character(
      UUID.randomUUID().toString,
      createInput.name, createInput.age.getOrElse(-1),
      createInput.ancestors.getOrElse(List.empty), createInput.descendants.getOrElse(List.empty)
    )
    for {
      insertedElement <- coll.insertOne(character).toFuture().recoverWith {case e : Throwable => Future.failed(e)}
    } yield {
      insertedElement
    }
  }

  override def update(updateInputTrait: UpdateInputTrait)(implicit ec: ExecutionContext): Future[Either[String, Boolean]] = {
    val updateInput = updateInputTrait.asInstanceOf[UpdateInput]
    for {
      oldDocument <- coll.find(equal("_id", updateInput._id)).toFuture().recoverWith{case e : Throwable => Future.failed(e)}
      oldCharacterOpt = oldDocument.headOption.map(Character.toCharacter)
      updatedCharacter <-
      if (oldCharacterOpt.isDefined) {
        val oldCharacter = oldCharacterOpt.get
        val newCharacter = oldCharacter.copy(
          _id = updateInput._id,
          name = updateInput.name.getOrElse(oldCharacter.name),
          age = updateInput.age.getOrElse(oldCharacter.age),
          ancestors = updateInput.ancestors.getOrElse(oldCharacter.ancestors),
          descendants = updateInput.descendants.getOrElse(oldCharacter.descendants)
        )
        coll.replaceOne(equal("_id", updateInput._id), newCharacter).toFuture().recoverWith {
          case e : Throwable => Future.failed(e)
        }.map(result => if(result.getMatchedCount == 1) true else false).map(Right(_))
      } else {
        Future.successful(Left(s"Character with _id ${updateInput._id} is not defined"))
      }
    } yield {
      updatedCharacter
    }
  }

  override def delete(deleteInputTrait: Core.DeleteInputTrait)(implicit ec: ExecutionContext): Future[Boolean] = {
    val deleteInput = deleteInputTrait.asInstanceOf[DeleteInput]
    for {
      deletedItem <- coll.deleteOne(equal("_id", deleteInput._id)).toFuture()
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

  override def updateAndFind(updateInputTrait: UpdateInputTrait)(implicit ec: ExecutionContext) : Future[Option[Character]] = {

    for {
      update <- update(updateInputTrait)
      find <- findById(updateInputTrait.asInstanceOf[UpdateInput]._id)
    } yield {
      find
    }
  }

}
