package pl.edu.pk.backend

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(HelloWorld::class.java, DeploymentOptions())
  println("Vert.x started") // TODO: log4j
}
