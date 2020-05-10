package pl.edu.pk.backend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StatusServiceTest {
  @Test
  fun `return status`() {
    val service = StatusService()

    val result = service.getStatus()

    assertThat(result.succeeded())
    assertThat(result.result().status).isEqualTo("ok")
  }
}
