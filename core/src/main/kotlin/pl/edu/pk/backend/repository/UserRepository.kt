package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.util.NoSuchResourceException

class UserRepository(private val pool: PgPool) {
  fun getUserByEmail(email: String): Future<SensitiveUser> {
    val promise = Promise.promise<SensitiveUser>()
    pool.preparedQuery("SELECT * FROM user_account WHERE email=$1", Tuple.of(email)) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        if (rows.size() == 0) {
          promise.fail(NoSuchResourceException("No such user with email: $email"))
        } else {
          promise.complete(mapUser(rows.first()))
        }
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun insertUser(firstName: String, lastName: String, email: String, password: String): Future<User> {
    val promise = Promise.promise<User>()
    pool.preparedQuery(
      "INSERT INTO user_account (first_name, last_name, email, password) VALUES($1, $2, $3, $4)",
      Tuple.of(firstName, lastName, email, password)
    ) { ar ->
      if (ar.succeeded()) {
        promise.complete(User(firstName, lastName, email, false))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  private fun mapUser(row: Row): SensitiveUser {
    return SensitiveUser(
      row.getInteger("id_user_account"),
      row.getString("first_name"),
      row.getString("last_name"),
      row.getString("email"),
      row.getString("password"),
      row.getBoolean("active"),
      row.getBoolean("disabled")
    )
  }
}
