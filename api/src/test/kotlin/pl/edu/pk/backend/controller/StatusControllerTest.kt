package pl.edu.pk.backend.controller

import io.mockk.mockk
import io.mockk.verify
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.Test
import pl.edu.pk.backend.service.StatusService


class StatusControllerTest {
  @Test
  fun `returns status`() {
    val context = mockk<RoutingContext>(relaxed = true)
    val service = mockk<StatusService>(relaxed = true)
    val controller = StatusController(service)

    controller.getStatus(context)

    verify { context.response().setStatusCode(200) }
  }
}
