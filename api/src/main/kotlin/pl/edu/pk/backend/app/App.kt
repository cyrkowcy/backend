package pl.edu.pk.backend.app

import io.vertx.core.Vertx
import pl.edu.pk.backend.Services

class App(
  val services: Services = Services(),
  val controllers: Controllers = Controllers(services),
  val vertx: Vertx = Vertx.vertx()
)
