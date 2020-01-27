package services

import javax.inject.Singleton

import models.Product
import models.events._

@Singleton
class ProductService() {
  var eventLog: Seq[ProductEvent] = Seq(
    ProductCreated("1", "iPhone 12", 100),
    ProductCreated("2", "iPhone 13", 200),
    ProductCreated("3", "iPhone 14", 250)
  )

  def list: List[Product] = {
    eventLog.foldLeft(List.empty[Product])((acc, event) => {
      event match {
        case ProductCreated(id, name, price) =>
          acc :+ Product(id, name, price)
        case ProductNameUpdated(id, name) =>
          acc.map(product => {
            if (product.id == id) Product(id, name, product.price)
            else product
          })
        case ProductPriceUpdated(id, price) =>
          acc.map(product => {
            if (product.id == id) Product(id, product.name, price)
            else product
          })
        case ProductDeleted(id) =>
          acc.filter(_.id != id)
      }
    })
  }

  def lookup(id: String) =
    list.find(_.id == id)

  def addToList(name: String, price: Int): Product = {
    val newId: String = (list.map(_.id.toInt).max + 1).toString
    val event = ProductCreated(newId, name, price)
    eventLog = eventLog :+ event
    Product(newId, name, price)
  }

  def update(id: String, name: String, price: Int): Option[Product] = {
    val updatedProduct = Product(id, name, price)
    list.find(_.id == id).map { productToUpdate =>
      eventLog = eventLog ++ Seq(
        ProductNameUpdated(id, name),
        ProductPriceUpdated(id, price)
      )
      updatedProduct
    }
  }

  /**
    * @return: the object I removed
    */
  def delete(id: String): Option[Product] = {
    list.find(_.id == id)
      .map { productToRemove =>
        eventLog = eventLog :+ ProductDeleted(id)
        productToRemove
      }
  }
}
