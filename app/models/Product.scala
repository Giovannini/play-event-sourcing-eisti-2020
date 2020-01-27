package models

import play.api.libs.json._

case class Product(
  id: String,
  name: String,
  price: Int
)

object Product {
  implicit val format = Json.format[Product]
}
