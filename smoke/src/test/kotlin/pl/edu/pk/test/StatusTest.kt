package pl.edu.pk.test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StatusTest {
  @Test
  fun statusTest() {
    val api = Api.create(Link.url)
    val response = api.getStatus().execute()
    assertThat(response.code()).isEqualTo(200)
  }
}
