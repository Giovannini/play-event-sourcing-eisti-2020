package services
import javax.inject.Singleton

import models.events.ProductEvent

@Singleton
class EventBus {
  var list: Seq[ProductEvent] = Nil
  var subscribers: Seq[ProductEvent => Unit] = Nil

  def publishEvent(event: ProductEvent): Unit = {
    list = list :+ event
    subscribers.foreach(callback => {
      callback(event)
    })
  }

  def subscribe(callback: ProductEvent => Unit): Unit = {
    subscribers = subscribers :+ callback
    list.foreach(event => {
      callback(event)
    })
  }
}
