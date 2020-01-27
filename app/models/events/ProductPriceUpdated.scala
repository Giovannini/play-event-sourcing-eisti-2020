package models.events

case class ProductPriceUpdated(
  id: String,
  price: Int
) extends ProductEvent
