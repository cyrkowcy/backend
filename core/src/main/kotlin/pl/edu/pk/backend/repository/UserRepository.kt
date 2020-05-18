package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.util.NoSuchResourceException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

class UserRepository(private val pool: PgPool) {
  fun getUsers(): Future<List<SensitiveUser>> {
    val promise = Promise.promise<List<SensitiveUser>>()
    pool.preparedQuery("SELECT * FROM user_account") { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        promise.complete(rows.map(::mapUser))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

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
    val createDate = OffsetDateTime.now()
    pool.preparedQuery(
      "INSERT INTO user_account (first_name, last_name, email, password, create_date) VALUES($1, $2, $3, $4, $5)",
      Tuple.of(firstName, lastName, email, password, createDate)
    ) { ar ->
      if (ar.succeeded()) {
        promise.complete(User(firstName, lastName, email, false, emptyList(),
          createDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun updateUser(
    targetEmail: String,
    newFirstName: String?,
    newLastName: String?,
    newEmail: String?,
    newPasswordHash: String?,
    newDisabled: Boolean?
  ): Future<Nothing> {
    val promise = Promise.promise<Nothing>()
    val counter = AtomicInteger(1)
    val updates = listOf(
      Pair("first_name", newFirstName),
      Pair("last_name", newLastName),
      Pair("email", newEmail),
      Pair("password", newPasswordHash),
      Pair("disabled", newDisabled)
    ).filter { it.second != null }
    val setExpr = updates.joinToString(", ") { "${it.first} = $${counter.getAndIncrement()}" }
    val updateValues = updates
      .map { it.second }
      .toMutableList()
      .apply { add(targetEmail) }
      .toTypedArray()
    pool.preparedQuery(
      "UPDATE user_account SET $setExpr WHERE email = $${counter.getAndIncrement()}",
      Tuple.wrap(*updateValues)
    ) { ar ->
      if (ar.succeeded()) {
        promise.complete()
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  companion object {
    fun mapUser(row: Row): SensitiveUser {
      return SensitiveUser(
        row.getInteger("id_user_account"),
        row.getString("first_name"),
        row.getString("last_name"),
        row.getString("email"),
        row.getString("password"),
        row.getBoolean("disabled"),
        emptyList(),
        row.getOffsetDateTime("create_date").format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
      )
    }
  }
}
