package pl.edu.pk.backend.app

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler

fun createRouter(vertx: Vertx, controllers: Controllers): Router {
  val router = Router.router(vertx)

  router.route().handler(AccessLogger()::handle)
  router.route().handler(BodyHandler.create())
  router.route().handler(createCorsHandler())

  with(controllers.statusController) {
    router.get("/status").handler(::getStatus)
  }

  with(controllers.userController) {
    router.get("/user").handler(::getUser)
    router.post("/user").handler(::postUser)
    router.post("/login").handler(::postLogin)
  }

  router.route().handler(StaticHandler.create().setCachingEnabled(false))

  return router
}

private fun createCorsHandler(): Handler<RoutingContext>? {
  return CorsHandler.create(".*.")
    .allowedMethod(HttpMethod.GET)
    .allowedMethod(HttpMethod.POST)
    .allowedMethod(HttpMethod.PATCH)
    .allowedMethod(HttpMethod.DELETE)
    .allowedMethod(HttpMethod.OPTIONS)
    .allowedHeader("Access-Control-Allow-Method")
    .allowedHeader("Access-Control-Allow-Origin")
    .allowedHeader("Access-Control-Allow-Credentials")
    .allowedHeader("Content-Type")
    .allowedHeader("Authorization")
    .allowCredentials(true)
}
