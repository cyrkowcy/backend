package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.TicketComment
import java.time.OffsetDateTime

class TicketCommentRepository(private val pool: PgPool) {
  fun getComments(ticketId: Int): Future<List<TicketComment>> {
    val promise = Promise.promise<List<TicketComment>>()
    pool.preparedQuery(
      """SELECT * FROM ticket_comment c
         LEFT JOIN user_account u ON c.user_account_id = u.id_user_account
         WHERE ticket_id = $1 ORDER BY c.create_date""".trimMargin(),
      Tuple.of(ticketId)
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

  fun insertComment(ticketId: Int, content: String, userId: Int): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    val createTime = OffsetDateTime.now()
    pool.preparedQuery("""INSERT INTO ticket_comment (content, user_account_id, ticket_id, create_date)
      Values($1, $2, $3, $4)""".trimMargin(), Tuple.of(content, userId, ticketId, createTime)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject()
          .put("content", content))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }
}

private fun mapComment(row: Row): TicketComment {
  return TicketComment(
    row.getInteger("id_ticket_comment"),
    row.getString("content"),
    UserRepository.mapUser(row),
    row.getOffsetDateTime("create_date")
  )
}
