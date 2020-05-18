package pl.edu.pk.backend.service

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.Point
import pl.edu.pk.backend.model.Route
import pl.edu.pk.backend.model.TripCommentDto
import pl.edu.pk.backend.model.TripDto
import pl.edu.pk.backend.model.RouteDto
import pl.edu.pk.backend.repository.TripCommentRepository
import pl.edu.pk.backend.repository.TripRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.AuthorizationException
import pl.edu.pk.backend.util.ValidationException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class TripService(
  private val tripRepository: TripRepository,
  private val userRepository: UserRepository,
  private val tripCommentRepository: TripCommentRepository
) {

  fun getRouteAndPoints(routeId: Int): Future<Pair<Route, List<Point>>> {
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

  fun getTrips(email: String): Future<List<TripDto>> {
    return tripRepository.getTripsByGuideEmail(email)
      .compose { trips ->
        CompositeFuture.all(trips.map { trip -> getTrip(email, trip.id) })
      }.map {
        it.list<TripDto>()
      }
  }

  fun createTrip(
    cost: String,
    description: String,
    peopleLimit: Int,
    date: String,
    active: Boolean,
    routeName: String,
    order1: Int,
    order2: Int,
    firstOrderPosition: String,
    secondOrderPosition: String,
    email: String
  ): Future<JsonObject> {
    if (cost.isBlank() or description.isBlank() or routeName.isBlank() or firstOrderPosition.isBlank()
      or secondOrderPosition.isBlank() or active == null) {
      return Future.failedFuture(ValidationException("Lack of inforamtions"))
    }
    if ((peopleLimit < 1)) {
      return Future.failedFuture(ValidationException("People Limit can't be smaller than 1"))
    }
    if (OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).isBefore(OffsetDateTime.now())) {
      return Future.failedFuture(ValidationException("Wrong date"))
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
          order1,
          order2,
          firstOrderPosition,
          secondOrderPosition
        )
      }
  }

  fun patchTrip(
    tripId: Int,
    newCost: String?,
    newDescription: String?,
    newPeopleLimit: Int?,
    newDateTrip: String?,
    active: Boolean?,
    newRouteName: String?,
    newFirstOrderPosition: String?,
    newSecondOrderPosition: String?
  ): Future<JsonObject> {
    var newDateTripOffset: OffsetDateTime? = null
    if (newDateTrip != null) {
      newDateTripOffset = OffsetDateTime.parse(newDateTrip)
      if (newDateTripOffset.isBefore(OffsetDateTime.now()))
        return Future.failedFuture(ValidationException("Wrong date."))
    }

    if ((newCost != null || newDescription != null || newPeopleLimit != null || newDateTripOffset != null ||
        active != null) &&
      (newRouteName != null || newRouteName != null || newFirstOrderPosition != null ||
        newSecondOrderPosition != null)) {
      if (newRouteName != null) {
        tripRepository.updateRoute(newRouteName, tripId)
      }
      if (newFirstOrderPosition != null) {
        tripRepository.updateCoordinate(newFirstOrderPosition, tripId, 1)
      }
      if (newSecondOrderPosition != null) {
        tripRepository.updateCoordinate(newSecondOrderPosition, tripId, 2)
      }
      return tripRepository.updateTrip(tripId, newCost, newDescription, newPeopleLimit, newDateTripOffset, active)
    }

    if (newCost != null || newDescription != null || newPeopleLimit != null || newDateTripOffset != null ||
      active != null) {
      return tripRepository.updateTrip(tripId, newCost, newDescription, newPeopleLimit, newDateTripOffset, active)
    }
    if (newRouteName != null) {
      return tripRepository.updateRoute(newRouteName, tripId)
    }
    if (newFirstOrderPosition != null) {
      return tripRepository.updateCoordinate(newFirstOrderPosition, tripId, 1)
    }
    if (newSecondOrderPosition != null) {
      return tripRepository.updateCoordinate(newSecondOrderPosition, tripId, 2)
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
