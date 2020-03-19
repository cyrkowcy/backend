package pl.edu.pk.backend.app

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

fun createRouter(vertx: Vertx, controllers: Controllers): Router {
  val router = Router.router(vertx)
  router.route().handler(AccessLogger()::handle)
  with(controllers.statusController) {
    router.get("/").handler(this::getStatus)
  }
  return router
}
