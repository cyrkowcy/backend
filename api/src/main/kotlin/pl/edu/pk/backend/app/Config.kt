package pl.edu.pk.backend.app

object Config {
  val appSecret = env("APP_SECRET")
  val profile = env("PROFILE")
  val database = Database()

  class Database {
    private val addressParts = env("DATABASE_HOST").split(":")
    val host = addressParts[0]
    val port = addressParts
      .getOrElse(1) { "5432" }
      .toIntOrNull()
      ?: error("Unable to parse database port")
    val name = env("DATABASE_NAME")
    val user = env("DATABASE_USER")
    val password = env("DATABASE_PASSWORD")
  }
}

private fun env(name: String): String {
  return System.getenv(name)
    ?: error("Missing required environment variable $name")
}
