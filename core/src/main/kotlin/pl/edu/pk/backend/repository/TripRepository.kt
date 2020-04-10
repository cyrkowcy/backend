package pl.edu.pk.backend.repository

import io.vertx.core.Promise
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import pl.edu.pk.backend.model.Trip
import java.util.concurrent.Future

class TripRepository(private val pool: PgPool) {
  fun getAllTrips(): Future<List<Trip>>{
    val query = "SELECT * FROM Trip tr"
    return getTrips(query, Tuple.tuple())
  }

  private fun getTrips(query: String, tuple: Tuple?): Future<List<Trip>> {
//    val promise = Promise.promise<List<Trip>>()
//    pool.preparedQuery(query, tuple) { ar ->
//      if (ar.succeeded()) {
//        val rows = ar.result()
//        promise.complete(rows.map(::mapTrip))
//      } else {
//        promise.fail(ar.cause())
//      }
//    }
//    return promise.future()
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
