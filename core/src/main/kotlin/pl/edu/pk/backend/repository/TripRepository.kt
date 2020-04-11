package pl.edu.pk.backend.repository

import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.Trip
import java.time.OffsetDateTime
import io.vertx.core.Future
import pl.edu.pk.backend.util.NoSuchResourceException

class TripRepository(private val pool: PgPool) {
  fun getAllTrips(): Future<List<Trip>>{
    val query = "SELECT * FROM Trip t"+
      "LEFT JOIN user_account u ON t.user_account_id = u.id_user_account"
    return getTrips(query, Tuple.tuple())
  }

  fun getTripByGuideEmail(email: String): Future<List<Trip>> {
    val query = "SELECT * FROM trips t LEFT JOIN user_account u ON t.user_account_id = u.id_user_account " +
      "WHERE t.id_trip = $1"
    return getTrips(query, Tuple.tuple())
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

  fun getTripById(tripId: Int): Future<Trip> {
    val query = "SELECT * FROM trips t WHERE t.id_trip = $1"
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
    userId:Int,
    routeId:Int,
    cost:String,
    description:String,
    peopleLimit:Int,
    date: OffsetDateTime
  ): Future<JsonObject> {
    val promise = Promise.promise<JsonObject>()
    pool.preparedQuery("INSERT INTO ticket (user_account_id, routeId, cost, description, people_limit, data_trip ) VALUES($1, $2, $3, $4, $5, $6)",
      Tuple.of(userId, routeId, cost, description, peopleLimit,date)) { ar ->
      if (ar.succeeded()) {
        promise.complete(JsonObject().put("description", description))
      } else {
        promise.fail(ar.cause())
      }
    }
    return promise.future()
  }

//  fun updateTrip(tripId: Int, newContent: String?, active: Boolean?): Future<JsonObject> {
//  }

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
