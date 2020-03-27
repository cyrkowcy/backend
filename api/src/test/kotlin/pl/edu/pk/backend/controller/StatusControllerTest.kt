package pl.edu.pk.backend.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.vertx.core.Future
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.Test
import pl.edu.pk.backend.model.ApiStatus
import pl.edu.pk.backend.service.StatusService

class StatusControllerTest {
  @Test
  fun `returns status`() {
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<StatusService>(relaxed = true)
    every { service.getStatus() } returns Future.succeededFuture(ApiStatus())
    val controller = StatusController(service)

    controller.getStatus(ctx)

    verify { ctx.response().setStatusCode(200) }
  }
}
