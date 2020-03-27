package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.Role

class RoleUserRepository(private val pool: PgPool) {
  fun getRoles(email: String): Future<List<Role>> {
    val promise = Promise.promise<List<Role>>()
    pool.preparedQuery(
      """SELECT role_id FROM role_user_account
          WHERE user_account_id = (SELECT id_user_account FROM user_account WHERE email=$1 AND disabled = false)"""
        .trimIndent(),
      Tuple.of(email)
    ) { ar ->
      if (ar.succeeded()) {
        val roles = ar.result()
          .mapNotNull { row ->
            Role.forId(row.getInteger("role_id"))
          }
        promise.complete(roles)
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun addRole(email: String, role: Role): Future<Nothing> {
    val promise = Promise.promise<Nothing>()
    pool.preparedQuery(
      """INSERT INTO role_user_account (user_account_id, role_id)
          VALUES((SELECT id_user_account FROM user_account WHERE email=$1), $2)""".trimIndent(),
      Tuple.of(email, role.id)
    ) { ar ->
      if (ar.succeeded()) {
        promise.complete()
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun removeRole(email: String, role: Role): Future<Nothing> {
    val promise = Promise.promise<Nothing>()
    pool.preparedQuery(
      """DELETE FROM role_user_account
          WHERE user_account_id = (SELECT id_user_account FROM user_account WHERE email=$1)
          AND role_id=$2""".trimIndent(),
      Tuple.of(email, role.id)
    ) { ar ->
      if (ar.succeeded()) {
        promise.complete()
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun removeRoles(email: String): Future<Nothing> {
    val promise = Promise.promise<Nothing>()
    pool.preparedQuery(
      """DELETE FROM role_user_account
          WHERE user_account_id = (SELECT id_user_account FROM user_account WHERE email=$1)""".trimIndent(),
      Tuple.of(email)
    ) { ar ->
      if (ar.succeeded()) {
        promise.complete()
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }
}
