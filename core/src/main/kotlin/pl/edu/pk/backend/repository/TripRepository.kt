package pl.edu.pk.backend.repository

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.Trip
import pl.edu.pk.backend.util.NoSuchResourceException
import java.text.FieldPosition
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

class TripRepository(private val pool: PgPool) {

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
    description: String,
    peopleLimit: Int,
    date: String,
    active: Boolean,
    routeName: String,
    firstOrderPosition: String,
    secondOrderPosition: String
  ): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    val dateOffset = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    //val promiseRoute: Promise<JsonObject> = Promise.promise<JsonObject>()
    pool.preparedQuery("INSERT INTO route (name) VALUES($1)",
    Tuple.of(routeName)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("name", routeName))
      } else {
        promise.fail(ar.cause())
      }
    }

    pool.preparedQuery("INSERT INTO point (order_position, coordinates, route_id) " +
      "VALUES(1, $1, (SELECT id_route FROM route r WHERE r.name = $2))",
    Tuple.of(firstOrderPosition, routeName)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("coordinates", firstOrderPosition))
      } else {
        promise.fail(ar.cause())
      }
    }

    pool.preparedQuery("INSERT INTO point (order_position, coordinates, route_id) " +
      "VALUES(2, $1, (SELECT id_route FROM route r WHERE r.name = $2))",
      Tuple.of(secondOrderPosition, routeName)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("coordinates", firstOrderPosition))
      } else {
        promise.fail(ar.cause())
      }
    }

    pool.preparedQuery("INSERT INTO trip (user_account_id, route_id, cost, description, people_limit, date_trip, active ) "+
      "VALUES($1, (SELECT id_route FROM route r WHERE r.name = $6) , $2, $3, $4, $5, $7)",
      Tuple.of(userId, cost, description, peopleLimit, dateOffset, routeName, active)) { ar ->
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
    newCost: String?,
    newDescription: String?,
    newPeopleLimit: Int?,
    newDateTrip: OffsetDateTime?,
    active: Boolean?
    //newRouteName: String?,
    //newFirstOrderPosition: String?,
    //newSecondOrderPosition: String?
  ):  Future<JsonObject> {
//    patchTrip(tripId, newCost, newDescription, newPeopleLimit, newDateTrip, active)
//    patchRoute(newRouteName, routeId)
    val promise = Promise.promise<JsonObject>()
    var counter = AtomicInteger(1)
    val updates = listOf(
      Pair("cost", newCost),
      Pair("description", newDescription),
      Pair("people_limit", newPeopleLimit),
      Pair("date_trip", newDateTrip),
      Pair("active", active)
      //Pair("routeName", newRouteName),
      //Pair("firstCoordinates", newFirstOrderPosition),
      //Pair("secondCoordinates", newSecondOrderPosition)
    ).filter { it.second != null }
    val setExpr = updates.joinToString(", ") { "${it.first} = $${counter.getAndIncrement()}" }
    val updateValues = updates
      .map { it.second }
      .toMutableList()
      .apply { add(tripId) }
      .toTypedArray()
    pool.preparedQuery(
      "UPDATE trip SET $setExpr WHERE id_trip = $${counter.getAndIncrement()}",
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

  fun updateRoute(newRouteName: String,tripId: Int): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    pool.preparedQuery("UPDATE route SET name = $1 WHERE route.id_route = (SELECT route_id from trip WHERE id_trip = $2)",
      Tuple.of(newRouteName, tripId)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("name", newRouteName))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

  fun updateCoordinate(newFirstOrderPosition: String, tripId: Int, pointNumber: Int): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    pool.preparedQuery("UPDATE point SET coordinates = $1 " +
      "WHERE point.route_id = (SELECT route_id FROM trip WHERE id_trip = $2) AND order_position =$3",
      Tuple.of(newFirstOrderPosition, tripId, pointNumber)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("coordinates", newFirstOrderPosition))
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
