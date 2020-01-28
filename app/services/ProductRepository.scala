package services

import javax.inject._

import models.Product
import models.events._
import services.EventLog

class ProductRepository @Inject()(
  eventLog: EventLog
) {
  private var state: Seq[Product] = Nil
  private var lastEventNumber: Int = 0

  def getState(): Seq[Product] = {
    if (hasStateChangedSinceLastRead) {
      updateState()
      state
    } else {
      state
    }
  }

  private def hasStateChangedSinceLastRead: Boolean = {
    eventLog.length > lastEventNumber
  }

  private def updateState(): Unit = {
    val newState = eventLog
      .allEventSince(lastEventNumber)
      .foldLeft(state)((acc, event) => {
        event match {
          case ProductCreated(id, name, price, version) =>
            acc :+ Product(id, name, price, version)
          case ProductNameUpdated(id, name, version) =>
            acc.map(product => {
              if (product.id == id) Product(id, name, product.price, version)
              else product
            })
          case ProductPriceUpdated(id, price, version) =>
            acc.map(product => {
              if (product.id == id) Product(id, product.name, price, version)
              else product
            })
          case ProductDeleted(id, _) =>
            acc.filter(_.id != id)
        }
      })
    lastEventNumber = eventLog.length
    state = newState
  }
}
