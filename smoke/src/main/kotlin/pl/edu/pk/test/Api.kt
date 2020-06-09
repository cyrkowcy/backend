package pl.edu.pk.test

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

  companion object {
    fun create(baseUrl: String): Api {
      val retrofit = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
      return retrofit.create(Api::class.java)
    }
  }

  @GET("/status")
  fun getStatus(): Call<ResponseBody>

  @Headers("Content-Type: application/json")
  @POST("login")
  fun getLogin(
    @Body user: JsonObject
  ): Call<ResponseBody>

  @Headers("Content-Type: application/json")
  @POST("user")
  fun createUser(
    @Body user: JsonObject
  ): Call<ResponseBody>

  @Headers("Content-Type: application/json")
  @GET("user")
  fun getCurrentUser(
    @Header("Authorization") token: String
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @POST("login")
  fun getLogin2(
    @Body user: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("user")
  fun getAllUser(
    @Header("Authorization") token: String
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @PATCH("user/{email}")
  fun patchUser(
    @Header("Authorization") token: String,
    @Path("email") email: String,
    @Body user: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("tickets")
  fun getTickets(
    @Header("Authorization") token: String,
    @Query("all") value: String
  ): Call<JsonArray>

  @Headers("Content-Type: application/json")
  @POST("tickets")
  fun postTickets(
    @Header("Authorization") token: String,
    @Body obj: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("tickets/{ticketId}")
  fun getTicketsId(
    @Header("Authorization") token: String,
    @Path("ticketId") value: Int
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @PATCH("tickets/{ticketId}")
  fun patchTicketsId(
    @Header("Authorization") token: String,
    @Path("ticketId") value: Int,
    @Body obj: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @POST("tickets/{ticketId}/comments")
  fun postTicketsIdComments(
    @Header("Authorization") token: String,
    @Path("ticketId") value: Int,
    @Body obj: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("user/trips")
  fun getUserTripsHistory(
    @Header("Authorization") token: String
  ): Call<JsonArray>

  @Headers("Content-Type: application/json")
  @POST("user/trips/{tripId}")
  fun postUserTripJoin(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @DELETE("user/trips/{tripId}")
  fun postUserTripDelete(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("user/trips/active")
  fun getUserTripAvailable(): Call<JsonArray>

  @Headers("Content-Type: application/json")
  @GET("trips")
  fun getTrips(
    @Header("Authorization") token: String
  ): Call<JsonArray>

  @Headers("Content-Type: application/json")
  @POST("trips")
  fun postTrips(
    @Header("Authorization") token: String,
    @Body obj: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("trips/{tripId}")
  fun getTripsTripId(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @PATCH("trips/{tripId}")
  fun patchTripsTripId(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int,
    @Body obj: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @GET("trips/{tripId}/comments")
  fun getTripsTripIdcomments(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int
  ): Call<JsonArray>

  @Headers("Content-Type: application/json")
  @POST("trips/{tripId}/comments")
  fun postTripsTripIdcomments(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int,
    @Body obj: JsonObject
  ): Call<JsonObject>

  @Headers("Content-Type: application/json")
  @PATCH("trips/{tripId}/comments/{commentId}")
  fun patchTripsTripIdcomments(
    @Header("Authorization") token: String,
    @Path("tripId") value: Int,
    @Path("commentId") value2: Int,
    @Body obj: JsonObject
  ): Call<JsonArray>
}
