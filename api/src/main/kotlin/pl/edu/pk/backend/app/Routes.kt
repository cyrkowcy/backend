package pl.edu.pk.backend.app

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler

fun createRouter(vertx: Vertx, controllers: Controllers): Router {
  val router = Router.router(vertx)

  router.route().handler(AccessLogger()::handle)
  router.route().handler(BodyHandler.create())

  with(controllers.statusController) {
    router.get("/status").handler(::getStatus)
  }

  with(controllers.userController) {
    router.get("/user").handler(::getUser)
    router.post("/user").handler(::postUser)
    router.post("/login").handler(::postLogin)
  }

  router.route().handler(StaticHandler.create())

  return router
}
