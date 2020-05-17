package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgException
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.model.TripComment
import pl.edu.pk.backend.util.NoSuchResourceException
import java.util.concurrent.atomic.AtomicInteger

class TripCommentRepository(private val pool: PgPool) {
  fun getComments(tripId: Int, includeRemoved: Boolean): Future<List<TripComment>> {
    val promise = Promise.promise<List<TripComment>>()
    pool.preparedQuery(
      """SELECT * FROM trip_comment c
         LEFT JOIN user_account u ON c.user_account_id = u.id_user_account
         WHERE trip_id = $1 AND deleted = $2""".trimMargin(),
      Tuple.of(tripId, includeRemoved)
    ) { ar ->
      if (ar.succeeded()) {
        val comments = ar.result()
        promise.complete(comments.map(::mapComment))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun getCommentAuthor(commentId: Int, tripId: Int): Future<SensitiveUser> {
    val promise = Promise.promise<SensitiveUser>()
    pool.preparedQuery(
      """SELECT * FROM user_account c
         JOIN trip_comment t ON t.user_account_id = c.id_user_account
         WHERE t.id_comment_user_account = $1 AND t.trip_id = $2""".trimMargin(),
      Tuple.of(commentId, tripId)
    ) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        if (rows.size() == 0) {
          promise.fail(NoSuchResourceException("No such trip comment with id: $commentId"))
        } else {
          promise.complete(mapCommentAuthor(rows.first()))
        }
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun insertComment(tripId: Int, content: String, userId: Int): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    val deleted = false
    pool.preparedQuery("""INSERT INTO trip_comment (content, user_account_id, trip_id, deleted)
      Values($1, $2, $3, $4)""".trimMargin(), Tuple.of(content, userId, tripId, deleted)) { ar ->
      if (ar.succeeded()) {
        promise.complete()
      } else {
        if (ar.cause() is PgException && (ar.cause() as PgException).code == "23503") {
          promise.fail(NoSuchResourceException("No such trip with id: $tripId"))
        } else {
          promise.fail(ar.cause())
        }
      }
    }
    return promise.future()
  }

  fun updateComment(commentId: Int, content: String?, deleted: Boolean?): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    var counter = AtomicInteger(1)
    val updates = listOf(
      Pair("content", content),
      Pair("deleted", deleted)
    ).filter { it.second != null }
    val setExpr = updates.joinToString(", ") { "${it.first} = $${counter.getAndIncrement()}" }
    val updateValues = updates
      .map { it.second }
      .toMutableList()
      .apply { add(commentId) }
      .toTypedArray()
    pool.preparedQuery(
      "UPDATE trip_comment SET $setExpr WHERE id_comment_user_account = $${counter.getAndIncrement()}",
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
}

private fun mapComment(row: Row): TripComment {
  return TripComment(
    row.getInteger("id_comment_user_account"),
    UserRepository.mapUser(row),
    row.getString("content"),
    row.getBoolean("deleted")
  )
}

private fun mapCommentAuthor(row: Row): SensitiveUser {
  return SensitiveUser(
    row.getInteger("id_user_account"),
    row.getString("first_name"),
    row.getString("last_name"),
    row.getString("email"),
    row.getString("password"),
    row.getBoolean("disabled"),
    emptyList()
  )
}
