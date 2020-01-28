package models.events

case class ProductDeleted(
  id: String,
  version: Int
) extends ProductEvent
