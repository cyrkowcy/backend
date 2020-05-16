package pl.edu.pk.backend.controller

import io.vertx.core.Future
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.apache.logging.log4j.LogManager
import pl.edu.pk.backend.util.ApiError
import pl.edu.pk.backend.util.ApiException

private val logger = LogManager.getLogger("handleResult")

private const val contentType = "application/json; charset=utf-8"

fun RoutingContext.failValidation(error: ApiError, details: String = "", code: Int = 400) {
  response()
    .setStatusCode(code)
    .putHeader("content-type", contentType)
    .end(
      Json.encodePrettily(
        mapOf(
          "error" to "ApiError",
          "message" to error.message,
          "details" to details
        )
      )
    )
}

fun <T> RoutingContext.handleResult(future: Future<T>) {
  future.setHandler { handler ->
    when (handler.succeeded()) {
      true -> {
        val result = handler.result()?.let { Json.encodePrettily(it) } ?: ""
        if (!result.isEmpty()) {
          response()
            .setStatusCode(200)
            .putHeader("content-type", contentType)
            .end(result)
        } else {
          response()
            .setStatusCode(204)
            .end(result)
        }
      }
      else -> {
        val cause = handler.cause()
        if (cause is ApiException) {
          if (cause.httpCode in 400..499) {
            handleError(false, cause, cause.httpCode)
          } else {
            handleError(true, cause, cause.httpCode)
          }
        } else {
          handleError(true, cause, 500)
        }
      }
    }
  }
}

fun RoutingContext.handleError(log: Boolean, cause: Throwable, httpCode: Int) {
  if (log) {
    logger.error("Error: ${cause.javaClass.simpleName}: ${cause.message}")
  }
  response()
    .setStatusCode(httpCode)
    .putHeader("content-type", contentType)
    .end(Json.encodePrettily(cause.toErrorResponse()))
}

private fun Throwable.toErrorResponse(): Map<String, String?> {
  return mapOf(
    "error" to javaClass.simpleName,
    "message" to message
  )
}

fun RoutingContext.safeBodyAsJson(): JsonObject? {
  return try {
    bodyAsJson ?: throw DecodeException("Missing body")
  } catch (e: DecodeException) {
    failValidation(ApiError.Body, e.message ?: "")
    return null
  }
}
