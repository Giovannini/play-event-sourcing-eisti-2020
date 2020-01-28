package models.events

case class ProductCreated(
  id: String,
  name: String,
  price: Int,
  version: Int
) extends ProductEvent
