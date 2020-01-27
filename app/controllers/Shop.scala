package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models.Product
import services.ProductService
import play.api.libs.json.Json

@Singleton
class Shop @Inject()(
  val controllerComponents: ControllerComponents,
  productService: ProductService
) extends BaseController {

  def listProducts = Action { request =>
    Ok(Json.toJson(productService.list))
  }

  def showProduct(id: String) = Action { implicit request =>
    productService.lookup(id) match {
      case None =>
        NotFound("Not found")
      case Some(product) =>
        Ok(Json.toJson(product))
    }
  }

}
