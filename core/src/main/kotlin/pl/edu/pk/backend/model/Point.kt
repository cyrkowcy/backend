package pl.edu.pk.backend.model

data class Point (
  val order: Int,
  val coordinates: String
) {
  fun toRoute(point: Point): Point {
    return Point(point.order, point.coordinates)
  }
}
