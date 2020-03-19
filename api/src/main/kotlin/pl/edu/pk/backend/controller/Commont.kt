package pl.edu.pk.backend.controller

import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("handleResult")

private const val contentType = "application/json; charset=utf-8"

fun <T> RoutingContext.handleResult(future: Future<T>) {
  future.setHandler { handler ->
    when (handler.succeeded()) {
      true -> {
        response()
          .setStatusCode(200)
          .putHeader("content-type", contentType)
          .end(Json.encodePrettily(handler.result()))
      }
      else -> {
        val cause = handler.cause()
        val message = "${cause.javaClass.simpleName}: ${cause.message}"
        logger.error("Error: $message")
        response()
          .setStatusCode(500)
          .putHeader("content-type", contentType)
          .end(Json.encodePrettily(mapOf("message" to message)))
      }
    }
  }
}
