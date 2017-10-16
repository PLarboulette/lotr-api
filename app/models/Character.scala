package models

import play.api.libs.json.{Json, Reads, Writes}

case class Character
(
  id : String,
  name : String,
  age : Option[Int],
  ancestors : List[String],
  descendants : List[String]
) {

}

object Character {

  implicit val reads: Reads[Character] = Json.reads[Character]
  implicit val writes: Writes[Character] = Json.writes[Character]
}

case class GetAll ()
case class GetById(id : String)

// Create messages
case class CreateInput (name : String, age : Option[Int], ancestors : Option[List[String]], descendants : Option[List[String]])
object CreateInput {
  implicit val reads: Reads[CreateInput] = Json.reads[CreateInput]
  implicit val writes: Writes[CreateInput] = Json.writes[CreateInput]
}

// Update messages
case class UpdateInput (id : Int, name : Option[String], age : Option[Int], ancestors : Option[List[String]], descendants : Option[List[String]])
object UpdateInput {
  implicit val reads: Reads[UpdateInput] = Json.reads[UpdateInput]
  implicit val writes: Writes[UpdateInput] = Json.writes[UpdateInput]
}

case class DeleteInput (id : String)
object DeleteInput {
  implicit val reads: Reads[DeleteInput] = Json.reads[DeleteInput]
  implicit val writes: Writes[DeleteInput] = Json.writes[DeleteInput]
}