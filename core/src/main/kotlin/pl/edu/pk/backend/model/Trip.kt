package pl.edu.pk.backend.model

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Trip(
  val id: Int,
  val routeId: Int,
  val cost: String,
  val description: String,
  val peopleLimit: Int,
  val dateTrip: OffsetDateTime,
  val active: Boolean,
  val guide: SensitiveUser,
  val comments: List<TripComment>
)

data class TripDto(
  val id: Int,
  val cost: String,
  val description: String,
  val peopleLimit: Int,
  val dateTrip: String,
  val active: Boolean,
  val guide: User,
  val route: RouteDto
) {
  companion object {
    fun from(trip: Trip, route: Route, points: List<Point>): TripDto {
      val routeDto: RouteDto = RouteDto.from(route, points)
      val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
      return TripDto(
        trip.id,
        trip.cost,
        trip.description,
        trip.peopleLimit,
        trip.dateTrip.format(formatter),
        trip.active,
        trip.guide.toUser(),
        routeDto
      )
    }
  }
}

data class TripWithComment(
  val ticket: TripDto,
  val comments: List<TripCommentDto>
) {
  companion object {
    fun from(trip: Trip, route: Route, points: List<Point>): TripWithComment {
      return TripWithComment(
        TripDto.from(trip, route, points),
        trip.comments.map { TripCommentDto.from(it) }
      )
    }
  }
}
