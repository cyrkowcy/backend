package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.service.StatusService

class StatusController(private val statusService: StatusService) {
  fun getStatus(ctx: RoutingContext) {
    ctx.handleResult(statusService.getStatus())
  }
}
