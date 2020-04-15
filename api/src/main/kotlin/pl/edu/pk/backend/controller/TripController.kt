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
    val routeId: Int = body.getInteger("routeId", 0)
    ctx.handleResult(tripService.createTrip(cost, routeId, description, peopleLimit, dateTrip, ctx.getCurrentUserEmail()))
  }

  fun patchTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val tripId = ctx.pathParam("tripId")
    val body = ctx.safeBodyAsJson() ?: return
    val newCost: String? = body.getString("cost")
    val newRouteId: Int? = body.getInteger("routeId")
    val newDescription: String? = body.getString("description")
    val newPeopleLimit: Int? = body.getInteger("peopleLimit")
    val newDateTrip: String? = body.getString("DateTrip")
    val active: Boolean? = body.getBoolean("active")
    if (listOf(newCost, newDescription, newRouteId, newPeopleLimit, newDateTrip, active).all { it == null }) {
      ctx.failValidation(ApiError.Body, "At least one parameter is required for trip patch")
      return
    }
    if (ctx.checkIfCurrentUserHasRole(Role.Guide)) {
      ctx.handleResult(tripService.patchTrip(
        tripId.toInt(),
        newRouteId,
        newCost,
        newDescription,
        newPeopleLimit,
        newDateTrip,
        active
      ))
    }
  }
}
