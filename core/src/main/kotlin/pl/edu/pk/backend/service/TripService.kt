package pl.edu.pk.backend.service

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.TripDto
import pl.edu.pk.backend.repository.TripRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.ValidationException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class TripService(
  private val tripRepository: TripRepository,
  private val userRepository: UserRepository
) {
  fun getTrips(): Future<List<TripDto>> {
    return tripRepository.getAllTrips().map { it.map { TripDto.from(it) } }
  }

  fun getTrip(email: String, tripId: Int): Future<TripDto> {
    return tripRepository.getTripByEmail(email, tripId).map { TripDto.from(it) }
  }

  fun getTrips(email: String): Future<List<TripDto>> {
    return tripRepository.getTripsByGuideEmail(email)
      .map { it.map { TripDto.from(it) } }
  }

  fun createTrip(cost: String,
                 routeId: Int,
                 description: String,
                 peopleLimit: Int,
                 date: String,
                 email: String
  ): Future<JsonObject> {
    if (cost.isBlank() or description.isBlank()) {
      return Future.failedFuture(ValidationException("Lack of inforamtions"))
    }
    if ((peopleLimit < 1)) {
      return Future.failedFuture(ValidationException("People Limit can't be smaller than 1"))
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    println(date)
    if (OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).isBefore(OffsetDateTime.now())) {
      return Future.failedFuture(ValidationException("Wrong date"))
    }
    return userRepository.getUserByEmail(email)
      .compose { user ->
        tripRepository.insertTrip(
          user.id,
          cost,
          routeId,
          description,
          peopleLimit,
          date
        )
      }
  }

  fun patchTrip(
    tripId: Int,
    routeId: Int?,
    newCost: String?,
    newDescription: String?,
    newPeopleLimit: Int?,
    newDateTrip: String?,
    active: Boolean?
  ): Future<JsonObject> {
    var newDateTripOffset: OffsetDateTime? = null
    if (newDateTrip != null) {
      newDateTripOffset = OffsetDateTime.parse(newDateTrip)
      if (newDateTripOffset.isBefore(OffsetDateTime.now()))
        return Future.failedFuture(ValidationException("Wrong Information."))
    }
    return tripRepository.updateTrip(tripId, routeId, newCost, newDescription, newPeopleLimit, newDateTripOffset, active)
  }
}
