package services

import javax.inject.Singleton

import models.events._

@Singleton
class EventLog {
  private var list: Seq[ProductEvent] = Nil

  def length: Int = list.length

  def allEventSince(lastEvent: Int): Seq[ProductEvent] = {
    list.drop(lastEvent)
  }

  def appendEvent(event: ProductEvent): Either[String, Unit] = {
    if (event.version == 1) {
      list = list :+ event
      Right({})
    } else {
      list
        .filter(_.id == event.id) // Seq[ProductEvent]
        .lastOption // Option[ProductEvent]
        .map(lastEvent => { // ProductEvent
          if(event.version == lastEvent.version + 1) {
            list = list :+ event
            Right({})
          } else {
            Left("Update did not work")
          }
        }) // Option[Either[String, Unit]]
        .getOrElse(Left(s"Product '${event.id}' not found"))
    }
  }
}
