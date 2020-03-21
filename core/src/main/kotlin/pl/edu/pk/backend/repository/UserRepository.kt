package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.util.NoSuchResourceException

class UserRepository(private val pool: PgPool) {
  fun getUserByEmail(email: String): Future<SensitiveUser> {
    val promise = Promise.promise<SensitiveUser>()
    pool.preparedQuery("SELECT * FROM user_account WHERE email=$1", Tuple.of(email)) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        if (rows.size() == 0) {
          promise.fail(NoSuchResourceException("No such user with email: $email"))
        }
        promise.complete(mapUser(rows.first()))
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
