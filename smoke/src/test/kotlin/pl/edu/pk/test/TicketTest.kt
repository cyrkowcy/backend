package pl.edu.pk.test

import com.google.gson.JsonObject
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TicketTest {
  val api = Api.create(Link.url)
  @Test
  fun getTicketsAll() {
    val response = api.getTickets(User.token, "true").execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun getTicketsFalse() {
    val response = api.getTickets(User.token, "false").execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun postTicket() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("content", "zgloszenie testowe")
    val response = api.postTickets(User.token, jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun getTicketId() {
    val response = api.getTicketsId(User.token, 4).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun patchTicketId() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("content", "zgloszenie testowe again")
    val response = api.patchTicketsId(User.token, 4, jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun postTicketsIdComment() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("content", "comment2")
    val response = api.postTicketsIdComments(User.token, 4, jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }
}
