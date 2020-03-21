package pl.edu.pk.backend

import io.vertx.pgclient.PgPool
import pl.edu.pk.backend.repository.UserRepository

class Repositories(private val pool: PgPool) {
  val userRepository by lazy {
    UserRepository(pool)
  }
}
