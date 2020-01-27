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

  def addProduct = Action { implicit request =>
    (for {
      json <- request.body.asJson
      name <- (json \ "name").validate[String].asOpt
      price <- (json \ "price").validate[Int].asOpt
    } yield {
      val newProduct =
        productService.addToList(name, price)
      Ok(Json.toJson(newProduct))
    }).getOrElse(BadRequest("Mauvais schéma JSON"))
  }

  def updateProduct(id: String) = Action { request =>
    (for {
      json <- request.body.asJson
      name <- (json \ "name").validate[String].asOpt
      price <- (json \ "price").validate[Int].asOpt
    } yield {
      val maybeUpdatedProduct =
        productService.update(id, name, price)
      maybeUpdatedProduct match {
        case Some(updatedProduct) =>
          Ok(Json.toJson(updatedProduct))
        case None =>
          NotFound("Not found")
      }
    }).getOrElse(BadRequest("Mauvais schéma JSON"))
  }

  def deleteProduct(id: String) = Action {
    productService.delete(id) match {
      case Some(deletedProduct) =>
        Ok(Json.toJson(deletedProduct))
      case None =>
        NotFound("Not found")
    }
  }

}
