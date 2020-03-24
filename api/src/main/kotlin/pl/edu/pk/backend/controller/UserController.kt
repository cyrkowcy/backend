package pl.edu.pk.backend.controller

import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.service.UserService
import pl.edu.pk.backend.util.ApiError

class UserController(private val userService: UserService) {
  fun getUser(context: RoutingContext) {
    val email = context.queryParam("email").firstOrNull()
    if (email == null) {
      context.failValidation(ApiError.Query, "email")
      return
    }
    context.handleResult(userService.getUserByEmail(email))
  }

  fun postLogin(context: RoutingContext) {
    val body = context.safeBodyAsJson() ?: return
    val email = body.getString("email", "")
    val password = body.getString("password", "")
    context.handleResult(userService.loginUser(email, password))
  }

  fun postUser(context: RoutingContext) {
    val body = context.safeBodyAsJson() ?: return
    val firstName = body.getString("firstName", "")
    val lastName = body.getString("lastName", "")
    val email = body.getString("email", "")
    val password = body.getString("password", "")
    context.handleResult(userService.createUser(firstName, lastName, email, password))
  }
}
