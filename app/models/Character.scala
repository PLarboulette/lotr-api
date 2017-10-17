package models

import models.Core.{CreateInputTrait, DeleteInputTrait, UpdateInputTrait}
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.libs.json.{Json, Reads, Writes} // Combinator syntax

case class OutputCharacter (_id : String, character : Character)

case class Character
(
  _id : String,
  name : String,
  age : Int,
  ancestors : List[String],
  descendants : List[String],
)

object Character {

  case class Find ()
  case class FindById(id : String)

  case class CreateInput (name : String, age : Option[Int], ancestors : Option[List[String]], descendants : Option[List[String]]) extends CreateInputTrait
  object CreateInput {
    implicit val reads: Reads[CreateInput] = Json.reads[CreateInput]
    implicit val writes: Writes[CreateInput] = Json.writes[CreateInput]
  }


  case class UpdateInput (_id : String, name : Option[String], age : Option[Int], ancestors : Option[List[String]], descendants : Option[List[String]]) extends UpdateInputTrait
  object UpdateInput {
    implicit val reads: Reads[UpdateInput] = Json.reads[UpdateInput]
    implicit val writes: Writes[UpdateInput] = Json.writes[UpdateInput]
  }

  case class DeleteInput (_id : String) extends DeleteInputTrait
  object DeleteInput {
    implicit val reads: Reads[DeleteInput] = Json.reads[DeleteInput]
    implicit val writes: Writes[DeleteInput] = Json.writes[DeleteInput]
  }

  implicit val reads: Reads[Character] = Json.reads[Character]
  implicit val writes: Writes[Character] = Json.writes[Character]

  implicit def toDocument (character : Character) : Document = {
    Document (
      "_id" -> character._id,
      "name" -> character.name,
      "age" -> character.age,
      "ancestors" -> character.ancestors,
      "descendants" -> character.descendants,
    )
  }

  implicit def toCharacter(document: Document): Character = {

    def convertArrayBSONToListString (array: Option[BsonValue]) : List[String] = {
      array.map(_.asArray().toArray().toList.map(_.asInstanceOf[BsonValue].asString().getValue)).getOrElse(List.empty)
    }

    Character(
      document.get("_id").map(_.asString().getValue).getOrElse(""),
        document.get("name").map(_.asString().getValue).getOrElse("N/A"),
      document.get("age").map(_.asInt32().getValue).getOrElse(-1),
      convertArrayBSONToListString(document.get("ancestors")),
      convertArrayBSONToListString(document.get("descendants"))
    )
  }


}

