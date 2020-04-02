package pl.edu.pk.backend

import io.vertx.core.Vertx
import pl.edu.pk.backend.service.JwtService
import pl.edu.pk.backend.service.StatusService
import pl.edu.pk.backend.service.TicketService
import pl.edu.pk.backend.service.UserService

class Services(private val vertx: Vertx, private val repositories: Repositories, private val jwtSecret: String) {
  val statusService by lazy {
    StatusService()
  }
  val userService by lazy {
    UserService(vertx, repositories.userRepository, repositories.roleUserRepository, jwtService)
  }
  val jwtService by lazy {
    JwtService(jwtSecret)
  }
  val ticketService by lazy {
    TicketService(repositories.ticketRepository, repositories.userRepository, repositories.ticketCommentRepository)
  }
}
