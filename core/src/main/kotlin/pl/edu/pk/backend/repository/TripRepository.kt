package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.Trip
import pl.edu.pk.backend.util.NoSuchResourceException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

class TripRepository(private val pool: PgPool) {

  private fun getId(email: String) {
    val query = "SELECT id_user_account FROM user_account  u WHERE u.email=$1"
  }

  fun getAllTrips(): Future<List<Trip>> {
    val query = "SELECT * FROM trip t"
    return getTrips(query, Tuple.tuple())
  }

  fun getTripsByGuideEmail(email: String): Future<List<Trip>> {
    val query = "SELECT * FROM trip t LEFT JOIN user_account u ON t.user_account_id = u.id_user_account " +
      "WHERE u.email = $1"
    return getTrips(query, Tuple.of(email))
  }

  private fun getTrips(query: String, tuple: Tuple?): Future<List<Trip>> {
    val promise = Promise.promise<List<Trip>>()
    pool.preparedQuery(query, tuple) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        promise.complete(rows.map(::mapTrip))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun getTripById(email: String, tripId: Int): Future<Trip> {
    val query = "SELECT * FROM trip t WHERE t.id_trip = $1"
    return getTrip(query, Tuple.of(tripId))
  }

  fun getTripByEmail(email: String, ticketId: Int): Future<Trip> {
    val query = "SELECT * FROM trip t " +
      "LEFT JOIN user_account u ON t.user_account_id = u.id_user_account WHERE t.id_trip = $1 and u.email = $2"
    return getTrip(query, Tuple.of(ticketId, email))
  }

  private fun getTrip(query: String, tuple: Tuple): Future<Trip> {
    val promise = Promise.promise<Trip>()
    pool.preparedQuery(query, tuple) { ar ->
      if (ar.succeeded()) {
        val rows = ar.result()
        if (rows.size() == 0) {
          promise.fail(NoSuchResourceException("No such trip with id: ${tuple.getInteger(0)}"))
        } else {
          promise.complete(TripRepository.mapTrip(rows.first()))
        }
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun insertTrip(
    userId: Int,
    cost: String,
    routeId: Int,
    description: String,
    peopleLimit: Int,
    date: String
  ): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    val dateOffset = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    pool.preparedQuery("INSERT INTO trip (user_account_id, route_id, cost, description, people_limit, date_trip ) VALUES($1, $2, $3, $4, $5, $6)",
      Tuple.of(userId, routeId, cost, description, peopleLimit, dateOffset)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("description", description))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun updateTrip(
    tripId: Int,
    newRouteId: Int?,
    newCost: String?,
    newDescription: String?,
    newPeopleLimit: Int?,
    newDateTrip: OffsetDateTime?,
    active: Boolean?
  ): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    val counter = AtomicInteger(1)
    val updates = listOf(
      Pair("routeId", newRouteId),
      Pair("cost", newCost),
      Pair("description", newDescription),
      Pair("people_limit", newPeopleLimit),
      Pair("date_trip", newDateTrip),
      Pair("active", active)
    ).filter { it.second != null }
    updates.forEach { println(it.first) }
    val setExpr = updates.joinToString(", ") { "${it.first} = $${counter.getAndIncrement()}" }
    println(setExpr)
    val updateValues = updates
      .map { it.second }
    var oneUser = ""
    val tuple = Tuple.wrap(updateValues).addInteger(tripId)
    pool.preparedQuery(
      "WITH rows AS(" +
        "UPDATE trip SET $setExpr FROM user_account WHERE id_trip = $${counter.getAndIncrement()} $oneUser" +
        " RETURNING 1)" +
        "SELECT COUNT(*) FROM rows",
      tuple) { ar ->
      if (ar.succeeded()) {
        val counter = ar.result().first().getValue(0)
        if (counter.toString() == "0") {
          promise.fail(NoSuchResourceException("No trip with id $tripId or you don't have rights to modify it."))
        } else {
          promise.complete(JsonObject()
            .put("routeId", newRouteId)
            .put("cost", newCost)
            .put("description", newDescription)
            .put("people_limit", newPeopleLimit)
            .put("date_trip", newDateTrip)
            .put("active", active)
          )
        }
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  companion object {
    fun mapTrip(row: Row): Trip {
      return Trip(
        row.getInteger("id_trip"),
        UserRepository.mapUser(row),
        row.getInteger("route_id"),
        row.getString("cost"),
        row.getString("description"),
        row.getInteger("people_limit"),
        row.getOffsetDateTime("date_trip"),
        row.getBoolean("active")
      )
    }
  }
}
