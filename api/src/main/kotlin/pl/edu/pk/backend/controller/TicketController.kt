package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.TicketService
import pl.edu.pk.backend.util.ApiError

class TicketController(private val ticketService: TicketService) {
  fun getTickets(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.User)) return
    var all = "false"
    try {
      all = ctx.queryParam("all")[0]
    } catch (ex: Exception) {
    }
    if (all == "true") {
      if (!ctx.checkCurrentUserHasRole(Role.Admin)) return
      ctx.handleResult(ticketService.getTickets())
    } else {
      ctx.handleResult(ticketService.getTicketsWrittenBy(ctx.getCurrentUserEmail()))
    }
  }

  fun postTicket(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.User)) return
    val body = ctx.safeBodyAsJson() ?: return
    val content = body.getString("content", "")
    val email = ctx.getCurrentUserEmail()
    ctx.handleResult(ticketService.createTicket(email, content))
  }

  fun pathTicket(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.User)) return
    val ticketId = ctx.pathParam("ticketId")
    val body = ctx.safeBodyAsJson() ?: return
    val newContent: String? = body.getString("content")
    val closed: Boolean? = body.getBoolean("closed")

    if (listOf(newContent, closed).all { it == null }) {
      ctx.failValidation(ApiError.Body, "At least one parameter is required for ticket patch")
      return
    }
    if (ctx.checkIfCurrentUserHasRole(Role.Admin)) {
      ctx.handleResult(ticketService.pathTicket(ticketId.toInt(), newContent, closed, null))
    } else {
      ctx.handleResult(ticketService.pathTicket(ticketId.toInt(), newContent, closed, ctx.getCurrentUserEmail()))
    }
  }
}
