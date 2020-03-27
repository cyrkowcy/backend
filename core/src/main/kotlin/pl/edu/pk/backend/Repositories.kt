package pl.edu.pk.backend

import io.vertx.pgclient.PgPool
import pl.edu.pk.backend.repository.RoleUserRepository
import pl.edu.pk.backend.repository.UserRepository

class Repositories(private val pool: PgPool) {
  val roleUserRepository by lazy {
    RoleUserRepository(pool)
  }
  val userRepository by lazy {
    UserRepository(pool)
  }
}
