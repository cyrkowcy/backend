package pl.edu.pk.backend.model

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


data class Trip (
  val id_trip: Int,
  val user_account_id: SensitiveUser,
  val route_id: Int,
  val cost: String,
  val description: String,
  val people_limit: Int,
  val date_trip: OffsetDateTime,
  val active: Boolean
)

data class TripDto (
  val id_trip: Int,
  val author: String,
  val date_trip: String,
  val description: String,
  val active: Boolean
) {
  companion object {
    fun from(trip: Trip): TripDto {
      val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
      return TripDto(
        trip.id_trip,
        trip.user_account_id.email,
        trip.description,
        trip.date_trip.format(formatter),
        trip.active
      )
    }
  }
}
