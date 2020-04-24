package pl.edu.pk.backend.app

import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions
import org.apache.logging.log4j.LogManager
import org.flywaydb.core.Flyway

private val logger = LogManager.getLogger("Database")

fun migrateDatabase() {
  logger.info("Migrating database")
  val config = Config.database
  Flyway.configure()
    .dataSource("jdbc:postgresql://${config.host}:${config.port}/${config.name}", config.user, config.password)
    .load()
    .migrate()
  logger.info("Database migrated")
}

fun createDatabasePool(): PgPool {
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
