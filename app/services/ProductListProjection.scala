package services

import javax.inject._

import models.Product
import models.events._
import services.EventBus

class ProductListProjection @Inject()(
  eventBus: EventBus
){
  var list: List[Product] = Nil
  eventBus.subscribe(handleEvent)

  private def handleEvent(event: ProductEvent) = {
    event match {
      case ProductCreated(id, name, price, version) =>
        list = list :+ Product(id, name, price, version)
      case ProductNameUpdated(id, name, version) =>
        list = list.map(product => {
          if (product.id == id) Product(id, name, product.price, version)
          else product
        })
      case ProductPriceUpdated(id, price, version) =>
        list = list.map(product => {
          if (product.id == id) Product(id, product.name, price, version)
          else product
        })
      case ProductDeleted(id, _) =>
        list = list.filter(_.id != id)
    }
  }
}
