package models.events

case class ProductPriceUpdated(
  id: String,
  price: Int,
  version: Int
) extends ProductEvent
