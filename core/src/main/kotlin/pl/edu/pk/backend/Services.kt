package pl.edu.pk.backend

import pl.edu.pk.backend.service.StatusService
import pl.edu.pk.backend.service.UserService

class Services(private val repositories: Repositories) {
  val statusService by lazy {
    StatusService()
  }
  val userService by lazy {
    UserService(repositories.userRepository)
  }
}
