package pl.edu.pk.test

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UserTripsTest {
  val api = Api.create(Link.url)

  @Test
  fun userHistory() {
    val response = api.getUserTripsHistory(User.token).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun joinTrip() {
    val response = api.postUserTripJoin(User.token, 5).execute()
    Assertions.assertThat(response.code()).isEqualTo(204)
  }

  @Test
  fun joinTrip404() {
    val response = api.postUserTripJoin(User.token, 1000000).execute()
    Assertions.assertThat(response.code()).isEqualTo(404)
  }

  @Test
  fun deleteFromTrip() {
    val response = api.postUserTripDelete(User.token, 5).execute()
    Assertions.assertThat(response.code()).isEqualTo(204)
  }

  @Test
  fun deleteFromTrip404() {
    val response = api.postUserTripDelete(User.token, 1000000).execute()
    Assertions.assertThat(response.code()).isEqualTo(404)
  }

  @Test
  fun getAvailable() {
    val response = api.getUserTripAvailable().execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }
}
