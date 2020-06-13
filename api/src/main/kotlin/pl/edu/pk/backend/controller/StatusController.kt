package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.service.StatusService
import pl.edu.pk.backend.model.Role

class StatusController(private val statusService: StatusService) {
  fun getStatus(ctx: RoutingContext) {
    ctx.handleResult(statusService.getStatus())
  }
  fun shutdown(ctx: RoutingContext) {
    if (ctx.checkCurrentUserHasRole(Role.Admin)) {
      ctx.handleResult(statusService.shutdown())
    }
  }
}
