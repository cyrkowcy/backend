package pl.edu.pk.test

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TripsTest {
  val api = Api.create(Link.url)

  @Test
  fun getCurrentUserTrips() {
    val response = api.getTrips(User.token).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun getCurrentUserTripsWWT() {
    val response = api.getTrips("").execute()
    Assertions.assertThat(response.code()).isEqualTo(400)
  }

  @Test
  fun createNewTrip() {
    val ss = "{" +
      "  \"cost\": 0," +
      "  \"description\": \"wycieczka dodana przez test\"," +
      "  \"peopleLimit\": 2," +
      "  \"dateTrip\": \"2022-06-01T14:00+01:00\"," +
      "  \"active\": false," +
      "  \"route\": {" +
      "    \"name\": \"string\"," +
      "    \"points\": [" +
      "      {" +
      "        \"order\": 0," +
      "        \"coordinates\": \"string\"" +
      "      }," +
      "      {" +
      "        \"order\": 1," +
      "        \"coordinates\": \"strin2g\"" +
      "      }" +
      "    ]" +
      "  }" +
      "}"
    val newJson = JsonParser().parse(ss).asJsonObject
    val response = api.postTrips(User.token, newJson).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun getCurrentUserTrip() {
    val response = api.getTripsTripId(User.token, 3).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun updateTrip() {
    val ss = "{" +
      "  \"cost\": 50" +
      "}"
    val newJson = JsonParser().parse(ss).asJsonObject
    val response = api.patchTripsTripId(User.token, 3, newJson).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun getAllComments() {
    val response = api.getTripsTripIdcomments(User.token, 3).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun createNewComment() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("content", "komentarz dodany przez test")
    val response = api.postTripsTripIdcomments(User.token, 3, jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun updateComment() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("content", "komenatrz updateowany przez test")
    val response = api.patchTripsTripIdcomments(User.token, 3, 3, jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(204)
  }
}
