package pl.edu.pk.backend.app

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("Router")

fun createRouter(vertx: Vertx, controllers: Controllers): Router {
  val router = Router.router(vertx)
  router.errorHandler(500) { rc: RoutingContext ->
    logger.error("Router handler error", rc.failure())
  }
  router.route().handler(AccessLogger())
  router.route().handler(createCorsHandler())
  router.route().handler(BodyHandler.create())
  router.route().handler(controllers.authorizationController)

  with(controllers.statusController) {
    router.get("/status").handler(::getStatus)
  }

  with(controllers.userController) {
    router.get("/user").handler(::getUser)
    router.get("/users").handler(::getUsers)
    router.post("/user").handler(::postUser)
    router.post("/login").handler(::postLogin)
    router.patch("/user/:email").handler(::patchUser)
  }

  with(controllers.ticketController) {
    router.get("/ticket").handler(::getTickets)
    router.get("/ticket/:ticketId").handler(::getTicket)
    router.post("/ticket").handler(::postTicket)
    router.post("/ticket/:ticketId/comment").handler(::postComment)
    router.patch("/ticket/:ticketId").handler(::patchTicket)
  }

  with(controllers.tripController) {
    router.get("/trips").handler(::getTrips)
    router.get("/trip/:tripId").handler(::getTrip)
    router.post("/trip").handler(::postTrip)
    router.patch("/trip/:tripId").handler(::patchTrip)
    router.get("/trip/:tripId/comments").handler(::getTripComments)
    router.post("/trip/:tripId/comment").handler(::createTripComment)
    router.patch("/trip/:tripId/comment/:commentId").handler(::patchTripComment)
  }

  router.route().handler(StaticHandler.create().setCachingEnabled(false))
  router.route("/").handler { ctx ->
    ctx.response()
      .setStatusCode(302)
      .putHeader("location", "/swagger/index.html")
      .end()
  }
  return router
}

private fun createCorsHandler(): Handler<RoutingContext> {
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
