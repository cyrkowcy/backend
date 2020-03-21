package pl.edu.pk.backend

import io.vertx.core.Vertx
import org.apache.logging.log4j.LogManager
import pl.edu.pk.backend.app.App
import pl.edu.pk.backend.app.Controllers
import pl.edu.pk.backend.app.createDatabaseClient
import pl.edu.pk.backend.app.createRouter

private val logger = LogManager.getLogger("Main")

fun main() {
  logger.info("App started")
  val vertx = Vertx.vertx()
  val database = createDatabaseClient()
  val repositories = Repositories(database)
  val services = Services(repositories)
  val controllers = Controllers(services)
  val app = App(services, controllers, repositories, database, vertx)
  startApp(app)
}

fun startApp(app: App) {
  val router = createRouter(app.vertx, app.controllers)
  app.vertx
    .createHttpServer()
    .requestHandler(router)
    .listen(8090)
  logger.info("HTTP server started")
}
