package pl.edu.pk.backend.model

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


data class Trip (
  val idTrip: Int,
  val userAccountId: SensitiveUser,
  val routeId: Int,
  val cost: String,
  val description: String,
  val peopleLimit: Int,
  val dateTrip: OffsetDateTime,
  val active: Boolean
)

data class TripDto (
  val idTrip: Int,
  val author: String,
  val dateTrip: String,
  val description: String,
  val active: Boolean
) {
  companion object {
    fun from(trip: Trip): TripDto {
      val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
      return TripDto(
        trip.idTrip,
        trip.userAccountId.email,
        trip.description,
        trip.dateTrip.format(formatter),
        trip.active
      )
    }
  }
}
