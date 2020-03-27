package pl.edu.pk.backend.app

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.apache.logging.log4j.LogManager

class AccessLogger : Handler<RoutingContext> {
  private val logger = LogManager.getLogger(this::class.java)

  override fun handle(ctx: RoutingContext) {
    ctx.addBodyEndHandler { log(ctx) }
    ctx.next()
  }

  private fun log(ctx: RoutingContext) {
    val timestamp = System.currentTimeMillis()
    val method = ctx.request().method()
    val uri = ctx.request().uri()
    val status = ctx.request().response().statusCode
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
