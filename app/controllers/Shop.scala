package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models.Product
import models.commands._
import services.ProductService
import services.ProductListProjection
import play.api.libs.json.Json

@Singleton
class Shop @Inject()(
  val controllerComponents: ControllerComponents,
  productService: ProductService,
  productListProjection: ProductListProjection
) extends BaseController {

  def listProducts = Action { request =>
    Ok(Json.toJson(productListProjection.list))
  }

  def showProduct(id: String) = Action { implicit request =>
    productListProjection.list.find(_.id == id) match {
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
      val command = CreateProduct(name, price)
      val newProduct =
        productService.addToList(command)
      Ok(Json.toJson(newProduct))
    }).getOrElse(BadRequest("Mauvais schéma JSON"))
  }

  def updateProduct(id: String) = Action { request =>
    (for {
      json <- request.body.asJson
      name <- (json \ "name").validate[String].asOpt
      price <- (json \ "price").validate[Int].asOpt
    } yield {
      val command = UpdateProduct(id, name, price)
      val maybeUpdatedProduct =
        productService.update(command)
      maybeUpdatedProduct match {
        case Some(updatedProduct) =>
          Ok(Json.toJson(updatedProduct))
        case None =>
          NotFound("Not found")
      }
    }).getOrElse(BadRequest("Mauvais schéma JSON"))
  }

  def deleteProduct(id: String) = Action {
    val command = DeleteProduct(id)
    productService.delete(command) match {
      case Some(deletedProduct) =>
        Ok(Json.toJson(deletedProduct))
      case None =>
        NotFound("Not found")
    }
  }

}
