package pl.edu.pk.backend.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.Test
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.TicketService
import pl.edu.pk.backend.util.ValidationException
import java.lang.StringBuilder

class TicketControllerTest {
  @Test
  fun `create ticket`() {
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<TicketService>(relaxed = true)
    val controller = TicketController(service)
    every { ctx.get<String>("currentUserEmail") } returns "test@example.com"
    every { service.createTicket(any(), any()) } returns
    Future.succeededFuture(JsonObject())
    every { ctx.get<List<Role>>("currentUserRoles") } returns listOf(Role.User)
    every { ctx.bodyAsJson } returns JsonObject(
      mapOf(
        "content" to "Ala ma kota"
      )
    )
    controller.postTicket(ctx)

    verify { service.createTicket("test@example.com", "Ala ma kota") }
    verify { ctx.response().setStatusCode(200) }
  }

  @Test
  fun `too long content`(){
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<TicketService>(relaxed = true)
    val controller = TicketController(service)
    val toLongString = StringBuilder()
    for(x in 0..1001) toLongString.append("a")
    every { ctx.get<String>("currentUserEmail") } returns "test@example.com"
    every { ctx.get<List<Role>>("currentUserRoles") } returns listOf(Role.User)
    every { service.createTicket(any(), any()) } returns
     Future.failedFuture(ValidationException(""))
    every { ctx.bodyAsJson } returns JsonObject(
      mapOf(
        "content" to toLongString.toString()
      )
    )
    controller.postTicket(ctx)

    verify { service.createTicket("test@example.com", toLongString.toString())}
    verify { ctx.response().setStatusCode(400)}
  }


  @Test
  fun `empty content`(){
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<TicketService>(relaxed = true)
    val controller = TicketController(service)
    every { ctx.get<String>("currentUserEmail") } returns "test@example.com"
    every { ctx.get<List<Role>>("currentUserRoles") } returns listOf(Role.User)
    every { service.createTicket(any(), any()) } returns
      Future.failedFuture(ValidationException(""))
    every { ctx.bodyAsJson } returns JsonObject(
      mapOf(
        "content" to ""
      )
    )
    controller.postTicket(ctx)

    verify { service.createTicket("test@example.com", "")}
    verify { ctx.response().setStatusCode(400)}
  }

}
