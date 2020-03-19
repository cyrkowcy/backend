package pl.edu.pk.backend

import org.apache.logging.log4j.LogManager
import pl.edu.pk.backend.app.App
import pl.edu.pk.backend.app.createRouter

private val logger = LogManager.getLogger("Main")

fun main() {
  logger.info("App started")
  val app = App()
  val router = createRouter(app.vertx, app.controllers)
  app.vertx
    .createHttpServer()
    .requestHandler(router)
    .listen(8090)
  logger.info("Vert.x started")
}
