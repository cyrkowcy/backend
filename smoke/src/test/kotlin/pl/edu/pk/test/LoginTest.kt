package pl.edu.pk.test

import com.google.gson.JsonObject
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoginTest {
  val api = Api.create(Link.url)

  @Test
  fun loginTestWrong() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("email", "1")
    jsonObject.addProperty("password", "1")
    val response = api.getLogin(jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(400)
  }

  @Test
  fun loginUserGoodButExists() {
    val jsonObject = JsonObject()
    jsonObject.addProperty("firstName", "test")
    jsonObject.addProperty("lastName", "test")
    jsonObject.addProperty("email", "test@test.pl")
    jsonObject.addProperty("password", "whatever")
    val response = api.createUser(jsonObject).execute()
    Assertions.assertThat(response.code()).isEqualTo(400)
  }

  @Test
  fun getCurentUser() {
    val response = api.getCurrentUser(User.token).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun getAllUsers() {
    val response = api.getAllUser(User.token).execute()
    Assertions.assertThat(response.code()).isEqualTo(200)
  }

  @Test
  fun patchUser() {
    val user = api.getCurrentUser(User.token).execute().body()
    val tmp = user?.get("email").toString()
    val email = tmp.substring(1, tmp.lastIndex)
    val json = JsonObject()
    val first = user?.get("firstName").toString().substring(1, user?.get("firstName").toString().lastIndex)
    val last = user?.get("lastName").toString().substring(1, user?.get("lastName").toString().lastIndex)
    json.addProperty("firstName", last)
    json.addProperty("lastName", first)
    val response = api.patchUser(User.token, email, json).execute()
    val usernew = api.getCurrentUser(User.token).execute().body()
    val firstNew = usernew?.get("firstName").toString().substring(1, usernew?.get("firstName").toString().lastIndex)
    val lastNew = usernew?.get("lastName").toString().substring(1, usernew?.get("lastName").toString().lastIndex)
    assertEquals(last, firstNew)
    assertEquals(first, lastNew)
    Assertions.assertThat(response.code()).isEqualTo(204)
  }
}
