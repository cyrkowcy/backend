package pl.edu.pk.backend.service

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.TripCommentDto
import pl.edu.pk.backend.model.TripDto
import pl.edu.pk.backend.model.Route
import pl.edu.pk.backend.model.RouteDto
import pl.edu.pk.backend.model.Point
import pl.edu.pk.backend.repository.TripCommentRepository
import pl.edu.pk.backend.repository.TripRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.AuthorizationException
import pl.edu.pk.backend.util.ValidationException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TripService(
  private val tripRepository: TripRepository,
  private val userRepository: UserRepository,
  private val tripCommentRepository: TripCommentRepository
) {

  private fun getRouteAndPoints(routeId: Int): Future<Pair<Route, List<Point>>> {
    return tripRepository.getRoute(routeId)
      .compose { tripRepository.getPoints(routeId).map { points -> Pair(it, points) } }
}

  private fun getRoutes(routeId: Int): Future<Route> {
    return tripRepository.getRoute(routeId).map { RouteDto.fromRoute(it) }
  }

  fun getTrip(email: String, tripId: Int): Future<TripDto> {
    return tripRepository.getTripByEmail(email, tripId).compose { getRouteAndPoints(it.routeId)
      .map { (route, points) ->
        TripDto.from(it, route, points) }
    }
  }

  private fun getTripById(tripId: Int): Future<TripDto> {
    return tripRepository.getTripByTripId(tripId).compose {
      getRouteAndPoints(it.routeId)
        .map { (route, points) ->
          TripDto.from(it, route, points)
        }
    }
  }

  fun getTrips(email: String): Future<List<TripDto>> {
    return tripRepository.getTripsByGuideEmail(email)
      .compose { trips ->
        CompositeFuture.all(trips.map { trip -> getTrip(email, trip.id) })
      }.map {
        it.list<TripDto>()
      }
  }

  private fun validateDate(dateToValidate: String): Boolean {
    val data: OffsetDateTime
    try {
        data = OffsetDateTime.parse(dateToValidate)
        return true
    } catch (e: DateTimeParseException) {
      return false
    }
  }

  fun createTrip(
    cost: String,
    description: String,
    peopleLimit: Int,
    date: String,
    active: Boolean,
    routeName: String,
    points: JsonArray,
    email: String
  ): Future<JsonObject> {
    if (cost.isBlank() or description.isBlank() or routeName.isBlank() or active == null) {
      return Future.failedFuture(ValidationException("Lack of informations"))
    }
    if ((peopleLimit < 1)) {
      return Future.failedFuture(ValidationException("People Limit can't be lower than 1"))
    }
    if (!validateDate(date) ||
      OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).isBefore(OffsetDateTime.now())) {
      return Future.failedFuture(ValidationException("Wrong date or format (yyyy-MM-ddTHH:mm+01:00)"))
    }
    return userRepository.getUserByEmail(email)
      .compose { user ->
        tripRepository.insertAll(
          user.id,
          cost,
          description,
          peopleLimit,
          date,
          active,
          routeName,
          points
        )
      }
  }

  fun patchTrip(
    body: JsonObject,
    tripId: Int,
    newCost: String?,
    newDescription: String?,
    newPeopleLimit: Int?,
    newDateTrip: String?,
    active: Boolean?
  ): Future<JsonObject> {
    var newDateTripOffset: OffsetDateTime? = null
    if (newDateTrip != null) {
      newDateTripOffset = OffsetDateTime.parse(newDateTrip)
      if (!validateDate(newDateTrip) || newDateTripOffset.isBefore(OffsetDateTime.now()))
        return Future.failedFuture(ValidationException("Wrong date."))
    }

    if (newCost != null || newDescription != null || newPeopleLimit != null || newDateTripOffset != null ||
      active != null) {
      tripRepository.updateTrip(tripId, newCost, newDescription, newPeopleLimit, newDateTripOffset, active)
    }
    if (body.containsKey("route")) {
      if (body.getJsonObject("route").containsKey("name")) {
        tripRepository.updateRoute(body.getJsonObject("route").getString("name"), tripId)
      }
      if(body.getJsonObject("route").containsKey("points")) {
        return tripRepository.updateCoordinate(body.getJsonObject("route").getJsonArray("points"), tripId)
      }
      return Future.future { JsonObject("""{"cost": $newCost, "description": $newDescription,
        | "peopleLimit": $newPeopleLimit, "date": $newDateTripOffset}""".trimMargin()) }
    }
    return Future.future()
  }

  fun createComment(tripId: Int, content: String, email: String): Future<JsonObject> {
    if (content.isBlank()) {
      return Future.failedFuture(ValidationException("Content is blank."))
    } else if (content.length > 1000) {
      return Future.failedFuture(ValidationException("Content is too long. Max content size 1000"))
    }
    return userRepository.getUserByEmail(email)
      .compose { tripCommentRepository.insertComment(tripId, content, it.id) }
  }

  fun patchComment(
    tripId: Int,
    commentId: Int,
    content: String? = null,
    deleted: Boolean? = null,
    email: String,
    isAdmin: Boolean
  ): Future<JsonObject> {
    return tripCommentRepository.getCommentAuthor(commentId, tripId)
      .compose { user ->
        if (email == user.email || isAdmin) {
          tripCommentRepository.updateComment(commentId, content, deleted)
        } else {
          Future.failedFuture(AuthorizationException("You don't have permission " +
            "to update comment: $commentId"))
        }
      }
  }

  fun getComments(tripId: Int): Future<List<TripCommentDto>> {
    return tripCommentRepository.getComments(tripId, false)
      .map { it.map { TripCommentDto.from(it) } }
  }
}
