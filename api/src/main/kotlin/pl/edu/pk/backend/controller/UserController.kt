package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.service.UserService

class UserController(private val userService: UserService) {
  fun getUser(context: RoutingContext) {
    val email = context.queryParam("email").firstOrNull()
    if (email == null) {
      context.response()
        .setStatusCode(400)
        .end()
      return
    }
    context.handleResult(userService.getUserByEmail(email))
  }
}
