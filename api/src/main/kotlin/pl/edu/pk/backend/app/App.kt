package pl.edu.pk.backend.app

import io.vertx.core.Vertx
import io.vertx.sqlclient.SqlClient
import pl.edu.pk.backend.Repositories
import pl.edu.pk.backend.Services

class App(
  val services: Services,
  val controllers: Controllers,
  val repositories: Repositories,
  val database: SqlClient,
  val vertx: Vertx
)
