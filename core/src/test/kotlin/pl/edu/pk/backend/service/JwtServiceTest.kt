package pl.edu.pk.backend.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JwtServiceTest {
  @Test
  fun `sign and verify token`() {
    val service = JwtService("foobar")
    val token = service.sign("foo@bar.com")
    assertThat(service.verify(token)).isNotNull()
  }

  @Test
  fun `reject malformed token`() {
    val service = JwtService("foobar")
    assertThat(service.verify("foo")).isNull()
  }

  @Test
  fun `reject invalid token`() {
    val service = JwtService("foobar")
    val badService = JwtService("barfoo")
    val badToken = badService.sign("foo@bar.com")
    assertThat(service.verify(badToken)).isNull()
  }
}
