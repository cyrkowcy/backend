package pl.edu.pk.backend.app

import pl.edu.pk.backend.Services
import pl.edu.pk.backend.controller.*

class Controllers(services: Services) {
  val authorizationController by lazy {
    AuthorizationController(services.userService, services.jwtService)
  }

  val statusController by lazy {
    StatusController(services.statusService)
  }

  val userController by lazy {
    UserController(services.userService)
  }

  val ticketController by lazy {
    TicketController(services.ticketService)
  }

  val tripController by lazy {
    TripController(services.tripService)
  }
}
