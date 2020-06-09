package pl.edu.pk.test

import com.google.gson.JsonObject

object User {
  val token: String

  init {
    val api = Api.create(Link.url)
    val jsonObject = JsonObject()
    val email = env("USER_EMAIL")
    val password = env("USER_PASSWORD")
    jsonObject.addProperty("email", email)
    jsonObject.addProperty("password", password)
    val response = api.getLogin2(jsonObject).execute()
    val jsonob = response.body()
    val tokenik = jsonob!!.get("token").toString()
    this.token = "Bearer " + tokenik.substring(1, tokenik.lastIndex)
  }
  private fun env(name: String): String {
    return System.getenv(name)
      ?: error("Missing required environment variable $name")
  }
}
