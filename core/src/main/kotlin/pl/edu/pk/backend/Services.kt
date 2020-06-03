package pl.edu.pk.backend

import io.vertx.core.Vertx
import pl.edu.pk.backend.service.JwtService
import pl.edu.pk.backend.service.StatusService
import pl.edu.pk.backend.service.TicketService
import pl.edu.pk.backend.service.TripService
import pl.edu.pk.backend.service.UserService

class Services(private val vertx: Vertx, private val repositories: Repositories, private val jwtSecret: String) {
  val statusService by lazy {
    StatusService()
  }
  val userService by lazy {
    UserService(
      vertx,
      repositories.userRepository,
      repositories.roleUserRepository,
      repositories.tripRepository,
      tripService,
      jwtService
    )
  }
  val jwtService by lazy {
    JwtService(jwtSecret)
  }
  val ticketService by lazy {
    TicketService(repositories.ticketRepository, userService, repositories.ticketCommentRepository)
  }
  val tripService by lazy {
    TripService(repositories.tripRepository, repositories.userRepository, repositories.tripCommentRepository)
  }
}
