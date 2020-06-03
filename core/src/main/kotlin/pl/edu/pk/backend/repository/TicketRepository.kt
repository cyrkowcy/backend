package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.Ticket
import pl.edu.pk.backend.model.TicketDto
import pl.edu.pk.backend.util.NoSuchResourceException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

class TicketRepository(private val pool: PgPool) {

  fun getAllTickets(): Future<List<Ticket>> {
    val query = "SELECT * FROM ticket t " +
      "LEFT JOIN user_account u ON t.user_account_id = u.id_user_account"
    return getTickets(query, Tuple.tuple())
  }

  fun getTicketsByUserEmail(email: String): Future<List<Ticket>> {
    val query = "SELECT * FROM ticket t " +
      "LEFT JOIN user_account u ON t.user_account_id = u.id_user_account WHERE u.email = $1"
    return getTickets(query, Tuple.of(email))
  }

  private fun getTickets(query: String, tuple: Tuple?): Future<List<Ticket>> {
    val promise = Promise.promise<List<Ticket>>()
    pool.preparedQuery(query, tuple) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        promise.complete(rows.map(::mapTicket))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun getTicket(ticketId: Int): Future<Ticket> {
    val query = "SELECT * FROM ticket t " +
      "LEFT JOIN user_account u ON t.user_account_id = u.id_user_account WHERE t.id_ticket = $1"
    return getTicket(query, Tuple.of(ticketId))
  }

  fun getTicketByEmail(email: String, ticketId: Int): Future<Ticket> {
    val query = "SELECT * FROM ticket t " +
      "LEFT JOIN user_account u ON t.user_account_id = u.id_user_account WHERE t.id_ticket = $1 and u.email = $2"
    return getTicket(query, Tuple.of(ticketId, email))
  }

  private fun getTicket(query: String, tuple: Tuple): Future<Ticket> {
    val promise = Promise.promise<Ticket>()
    pool.preparedQuery(query, tuple) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        if (rows.size() == 0) {
          promise.fail(NoSuchResourceException("No such ticket with id: ${tuple.getInteger(0)}"))
        } else {
          promise.complete(mapTicket(rows.first()))
        }
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun insertTicket(userId: Int, content: String, email: String): Future<TicketDto> {
    val promise = Promise.promise<TicketDto>()
    val createTime = OffsetDateTime.now()
    pool.preparedQuery(
      "INSERT INTO ticket (user_account_id, content, create_date)" +
        " VALUES($1, $2, $3) RETURNING id_ticket",
      Tuple.of(userId, content, createTime)
    ) { ar ->
      if (ar.succeeded()) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val ticketId = ar.result().first().getInteger("id_ticket")
        promise.complete(
          TicketDto(
            ticketId,
            false,
            email,
            createTime.format(formatter),
            content
          )
        )
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun update(ticketId: Int, newContent: String?, closed: Boolean?, email: String?): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    val counter = AtomicInteger(1)
    val updates = listOf(
      Pair("content", newContent),
      Pair("closed", closed)
    ).filter { it.second != null }
    val setExpr = updates.joinToString(", ") { "${it.first} = $${counter.getAndIncrement()}" }
    val updateValues = updates
      .map { it.second }
    var oneUser = ""
    val tuple = Tuple.wrap(updateValues).addInteger(ticketId)
    if (email != null) {
      oneUser = "and user_account_id = id_user_account and email = $${counter.get() + 1}"
      tuple.addString(email)
    }
    pool.preparedQuery(
      "WITH rows AS(" +
        "UPDATE ticket SET $setExpr FROM user_account WHERE id_ticket = $${counter.getAndIncrement()} $oneUser" +
        " RETURNING 1)" +
        "SELECT COUNT(*) FROM rows",
      tuple
    ) { ar ->
      if (ar.succeeded()) {
        val localCounter = ar.result().first().getValue(0)
        if (localCounter.toString() == "0") {
          promise.fail(NoSuchResourceException("No ticket with id $ticketId or you don't have rights to modify it."))
        } else {
          promise.complete(
            JsonObject()
              .put("content", newContent)
              .put("closed", closed)
          )
        }
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  companion object {
    fun mapTicket(row: Row): Ticket {
      return Ticket(
        row.getInteger("id_ticket"),
        UserRepository.mapUser(row),
        row.getString("content"),
        row.getBoolean("closed"),
        row.getOffsetDateTime("create_date"),
        emptyList()
      )
    }
  }
}
