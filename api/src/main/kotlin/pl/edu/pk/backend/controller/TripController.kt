package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.TripService
import pl.edu.pk.backend.util.ApiError

class TripController(private val tripService: TripService) {
  fun getTrips(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    if (ctx.checkCurrentUserHasRole(Role.Guide)) {
      ctx.handleResult(tripService.getTrips(ctx.getCurrentUserEmail()))
    }
  }

  fun getTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val tripId = ctx.pathParam("tripId")
    if (ctx.checkCurrentUserHasRole(Role.Guide)) {
      ctx.handleResult(tripService.getTrip(ctx.getCurrentUserEmail(), tripId.toInt()))
    }
  }

  fun postTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val body = ctx.safeBodyAsJson() ?: return
    val cost = body.getString("cost", "")
    val description = body.getString("description", "")
    val peopleLimit = body.getInteger("peopleLimit", 1)
    val dateTrip = body.getString("dateTrip", "")
    val active: Boolean = body.getBoolean("active", false)
    val routeName: String = body.getString("routeName", "")
    val firstOrderPosition = body.getString("firstCoordinates", "")
    val secondOrderPosition = body.getString("secondCoordinates", "")
    ctx.handleResult(tripService.createTrip(cost, description, peopleLimit, dateTrip, active, routeName, firstOrderPosition, secondOrderPosition, ctx.getCurrentUserEmail()))
  }

  fun patchTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val tripId = ctx.pathParam("tripId")
    val body = ctx.safeBodyAsJson() ?: return
    val newCost: String? = body.getString("cost")
    val newDescription: String? = body.getString("description")
    val newPeopleLimit: Int? = body.getInteger("peopleLimit")
    val newDateTrip: String? = body.getString("DateTrip")
    val active: Boolean? = body.getBoolean("active")
    val newRouteName: String? = body.getString("routeName")
    val newFirstOrderPosition: String? = body.getString("firstCoordinates")
    val newSecondOrderPosition: String? = body.getString("secondCoordinates")
    if (listOf(newCost, newDescription, newRouteName, newPeopleLimit, newDateTrip, active, newRouteName, newFirstOrderPosition, newSecondOrderPosition).all { it == null }) {
      ctx.failValidation(ApiError.Body, "At least one parameter is required for trip patch")
      return
    }
    if (ctx.checkIfCurrentUserHasRole(Role.Guide)) {
      ctx.handleResult(tripService.patchTrip(
        tripId.toInt(),
        newCost,
        newDescription,
        newPeopleLimit,
        newDateTrip,
        active,
        newRouteName,
        newFirstOrderPosition,
        newSecondOrderPosition
      ))
    }
  }
}
