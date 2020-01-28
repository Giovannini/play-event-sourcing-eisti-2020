package models.events

trait ProductEvent {
  def id: String
  def version: Int
}
