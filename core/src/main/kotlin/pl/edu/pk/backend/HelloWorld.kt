package pl.edu.pk.backend

import io.vertx.core.AbstractVerticle

class HelloWorld : AbstractVerticle() {
  override fun start() {
    vertx.createHttpServer()
      .requestHandler { req ->
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x")
      }.listen(8080)
  }
}
