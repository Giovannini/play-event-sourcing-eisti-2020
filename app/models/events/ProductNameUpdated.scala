package models.events

case class ProductNameUpdated(
  id: String,
  name: String
) extends ProductEvent
