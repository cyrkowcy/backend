package pl.edu.pk.backend.app

import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions

fun createDatabaseClient(): PgPool {
  val config = Config.database
  val connectOptions = PgConnectOptions()
    .setHost(config.host)
    .setPort(config.port)
    .setDatabase(config.name)
    .setUser(config.user)
    .setPassword(config.password)
  val poolOptions = PoolOptions()
    .setMaxSize(5)
  return PgPool.pool(connectOptions, poolOptions)
}
