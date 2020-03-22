package pl.edu.pk.backend.app

import io.vertx.ext.web.RoutingContext
import org.apache.logging.log4j.LogManager

class AccessLogger {
  private val logger = LogManager.getLogger(this::class.java)

  fun handle(context: RoutingContext) {
    context.addBodyEndHandler { log(context) }
    context.next()
  }

  private fun log(context: RoutingContext) {
    val timestamp = System.currentTimeMillis()
    val method = context.request().method()
    val uri = context.request().uri()
    val status = context.request().response().statusCode
    val message = String.format(
      "%s %s %d - %d ms",
      method, uri, status, System.currentTimeMillis() - timestamp
    )
    logMessage(status, message)
  }

  private fun logMessage(status: Int, message: String) {
    when {
      status >= 500 -> logger.error(message)
      status >= 400 -> logger.warn(message)
      else -> logger.info(message)
    }
  }
}
