package models.events

case class ProductNameUpdated(
  id: String,
  name: String,
  version: Int
) extends ProductEvent
