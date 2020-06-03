package pl.edu.pk.backend.controller

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.TripService
import pl.edu.pk.backend.util.ApiError

class TripController(private val tripService: TripService) {
  fun getTrips(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    ctx.handleResult(tripService.getTrips(ctx.getCurrentUserEmail()))
  }

  fun getTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val tripId = ctx.pathParam("tripId")
    ctx.handleResult(tripService.getTrip(ctx.getCurrentUserEmail(), tripId.toInt()))
  }

  fun postTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val body = ctx.safeBodyAsJson() ?: return
    val iCost = body.getInteger("cost", 1)
    val description = body.getString("description", "")
    val peopleLimit = body.getInteger("peopleLimit", 1)
    val dateTrip = body.getString("dateTrip", "")
    val active: Boolean = body.getBoolean("active", false)
    val route: JsonObject = body.getJsonObject("route")
    val routeName: String = route.getString("name", "")
    val points: JsonArray = route.getJsonArray("points")

    if (points.size() < 2) {
      ctx.failValidation(ApiError.Body, "At least two points required")
      return
    }

    repeat(points.size()) {
      if (points.getJsonObject(it).getInteger("order") == null ||
        points.getJsonObject(it).getString("coordinates") == null
      ) {
        ctx.failValidation(ApiError.Body, "Invalid point data, specify order and coordinates")
        return
      }
    }

    val cost = iCost.toString()

    ctx.handleResult(
      tripService.createTrip(
        cost,
        description,
        peopleLimit,
        dateTrip,
        active,
        routeName,
        points,
        ctx.getCurrentUserEmail()
      )
    )
  }

  fun patchTrip(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val tripId = ctx.pathParam("tripId")
    val body = ctx.safeBodyAsJson() ?: return
    val newICost: Int? = body.getInteger("cost")
    val newDescription: String? = body.getString("description")
    val newPeopleLimit: Int? = body.getInteger("peopleLimit")
    val newDateTrip: String? = body.getString("dateTrip")
    val active: Boolean? = body.getBoolean("active")
    val newRoute: JsonObject? = body.getJsonObject("route")

    val newCost: String? = newICost.toString()

    if (listOf(
        newCost,
        newDescription,
        newPeopleLimit,
        newDateTrip,
        active,
        newRoute
      ).all { it == null }
    ) {
      ctx.failValidation(ApiError.Body, "At least one parameter is required for trip patch")
      return
    }
    ctx.handleResult(
      tripService.patchTrip(
        body,
        tripId.toInt(),
        newCost,
        newDescription,
        newPeopleLimit,
        newDateTrip,
        active,
        ctx.getCurrentUserEmail()
      )
    )
  }

  fun createTripComment(ctx: RoutingContext) {
    val tripId = ctx.pathParam("tripId")
    val body = ctx.safeBodyAsJson() ?: return
    val content = body.getString("content", "")
    ctx.handleResult(tripService.createComment(tripId.toInt(), content, ctx.getCurrentUserEmail()))
  }

  fun getTripComments(ctx: RoutingContext) {
    val tripId = ctx.pathParam("tripId")
    ctx.handleResult(tripService.getComments(tripId.toInt()))
  }

  fun patchTripComment(ctx: RoutingContext) {
    val tripId = ctx.pathParam("tripId")
    val commentId = ctx.pathParam("commentId")
    val body = ctx.safeBodyAsJson() ?: return
    val content: String? = body.getString("content")
    val deleted: Boolean? = body.getBoolean("deleted")
    if (listOf(content, deleted).all { it == null }) {
      ctx.failValidation(ApiError.Body, "At least one parameter is required for trip comment patch")
      return
    }
    ctx.handleResult(
      tripService.patchComment(
        tripId.toInt(), commentId.toInt(), content, deleted,
        ctx.getCurrentUserEmail(),
        ctx.checkIfCurrentUserHasRole(Role.Admin)
      )
    )
  }
}
