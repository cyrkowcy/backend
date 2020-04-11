package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.TripService

class TripController(private val tripService: TripService) {
  fun getTrip(ctx: RoutingContext){
    if(!ctx.checkCurrentUserHasRole(Role.Guide)) {
      return
    }
    val tripId = ctx.pathParam("tripId")
    if(ctx.checkCurrentUserHasRole(Role.Guide)) {
      ctx.handleResult(tripService.getTrip(ctx.getCurrentUserEmail()))
    }
  }
}
