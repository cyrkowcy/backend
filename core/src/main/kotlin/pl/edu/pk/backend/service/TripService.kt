package pl.edu.pk.backend.service

import pl.edu.pk.backend.model.TripDto
import pl.edu.pk.backend.repository.TripRepository
import pl.edu.pk.backend.repository.UserRepository
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.Trip
import pl.edu.pk.backend.util.ValidationException
import java.time.OffsetDateTime


class TripService(
  private val tripRepository: TripRepository,
  private val userRepository: UserRepository
) {
  fun getTrips(): Future<List<TripDto>> {
    return tripRepository.getAllTrips().map { it.map {TripDto.from(it)}}
  }

//  fun getTrip(tripId:Int): Future<TripDto> {
//    return tripRepository.getTripById(tripId)
//      .map { it.map { TripDto.from(it) } }
//  }

  fun getTrip(email: String): Future<List<TripDto>> {
    return tripRepository.getTripByGuideEmail(email)
      .map{ it.map { TripDto.from(it) }}
  }

  fun createTrip(cost: String,
                 description: String,
                 peopleLimit:Int,
                 date: OffsetDateTime,
                 email: String,
                 routeId: Int
  ): Future<JsonObject> {
    if (cost.isBlank() or description.isBlank()) {
      return Future.failedFuture(ValidationException("Lack of inforamtions"))
    }
    if((peopleLimit<1)) {
      return Future.failedFuture(ValidationException("People Limit can't be smaller than 1"))
    }
    if((date.isBefore(OffsetDateTime.now()))) {
      return Future.failedFuture(ValidationException("Wrong date"))
    }
    return userRepository.getUserByEmail(email)
      .compose{ user -> tripRepository.insertTrip(
      user.id,
      routeId,
      cost,
      description,
      peopleLimit,
      date
    )}
  }

  //fun patchTrip(tripId:Int)
}
