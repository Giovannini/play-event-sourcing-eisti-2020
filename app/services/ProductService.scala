package services

import javax.inject.Singleton

import models.Product

@Singleton
class ProductService() {
  val list: Seq[Product] = Seq(
    Product("1", "iPhone 12", 100),
    Product("2", "iPhone 13", 200),
    Product("3", "iPhone 14", 250)
  )

  def lookup(id: String) =
    list.find(_.id == id)
}
