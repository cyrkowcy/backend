package pl.edu.pk.backend.app

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler

fun createRouter(vertx: Vertx, controllers: Controllers): Router {
  val router = Router.router(vertx)

  router.route().handler(AccessLogger()::handle)

  with(controllers.statusController) {
    router.get("/status").handler(::getStatus)
  }

  with(controllers.userController) {
    router.get("/user").handler(::getUser)
  }

  router.route().handler(StaticHandler.create())

  return router
}
