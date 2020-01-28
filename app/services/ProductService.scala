package services

import javax.inject._

import models.Product
import models.events._
import models.commands._
import services.{EventBus, ProductRepository}

@Singleton
class ProductService @Inject()(
  eventLog: EventLog,
  productRepository: ProductRepository,
  eventBus: EventBus
) {
  // Initialize event log
  Seq(
    ProductCreated("1", "iPhone 12", 100, 1),
    ProductCreated("2", "iPhone 13", 200, 1),
    ProductCreated("3", "iPhone 14", 250, 1)
  ).foreach(saveAndPublish)

  // TODO: extract in a projection
  def lookup(id: String) =
    productRepository.getState().find(_.id == id)

  def addToList(command: CreateProduct): Product = {
    val newId: String = {
      (productRepository.getState().map(_.id.toInt).max + 1)
        .toString
    }
    saveAndPublish(ProductCreated(newId, command.name, command.price, 1))
    Product(newId, command.name, command.price, 1)
  }

  def update(command: UpdateProduct): Option[Product] = {
    productRepository.getState().find(_.id == command.id).map { productToUpdate =>
      Seq(
        ProductNameUpdated(command.id, command.name, productToUpdate.version + 1),
        ProductPriceUpdated(command.id, command.price, productToUpdate.version + 2)
      ).foreach(saveAndPublish)
      Product(
        command.id,
        command.name,
        command.price,
        productToUpdate.version + 2
      )
    }
  }

  /**
    * @return: the object I removed
    */
  def delete(command: DeleteProduct): Option[Product] = {
    productRepository.getState().find(_.id == command.id)
      .map { productToRemove =>
        saveAndPublish(ProductDeleted(command.id, productToRemove.version + 1))
        productToRemove
      }
  }

  private def saveAndPublish(event: ProductEvent) = {
    eventLog.appendEvent(event) match {
      case Left(error) =>
        throw new Exception(error)
      case Right(_) =>
        eventBus.publishEvent(event)
    }
  }
}
