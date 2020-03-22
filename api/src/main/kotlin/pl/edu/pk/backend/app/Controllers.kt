package pl.edu.pk.backend.app

import pl.edu.pk.backend.Services
import pl.edu.pk.backend.controller.StatusController
import pl.edu.pk.backend.controller.UserController

class Controllers(services: Services) {
  val statusController by lazy {
    StatusController(services.statusService)
  }

  val userController by lazy {
    UserController(services.userService)
  }
}
