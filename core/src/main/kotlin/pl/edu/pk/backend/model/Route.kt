package pl.edu.pk.backend.model

data class Route(
  val name: String
)

data class RouteDto(
  val name: String,
  val points: List<Point>
) {
  companion object {
    fun from(route: Route, points: List<Point>): RouteDto {
      return RouteDto(route.name, points)
    }

    fun fromRoute(route: Route): Route {
      return Route(route.name)
    }
  }
}
