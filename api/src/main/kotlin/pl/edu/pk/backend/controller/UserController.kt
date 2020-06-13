package pl.edu.pk.backend.controller

import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.UserService
import pl.edu.pk.backend.util.ApiError

class UserController(private val userService: UserService) {
  fun getUsers(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.Admin)) {
      return
    }
    ctx.handleResult(userService.getUsers())
  }

  fun getUser(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserHasRole(Role.User)) {
      return
    }
    val email = ctx.getCurrentUserEmail()
    ctx.handleResult(userService.getUserByEmail(email))
  }

  fun postLogin(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserNone()) {
      return
    }
    val body = ctx.safeBodyAsJson() ?: return
    val email = body.getString("email", "")
    val password = body.getString("password", "")
    ctx.handleResult(userService.loginUser(email, password))
  }

  fun postUser(ctx: RoutingContext) {
    if (!ctx.checkCurrentUserNone()) {
      return
    }
    val body = ctx.safeBodyAsJson() ?: return
    val firstName = body.getString("firstName", "")
    val lastName = body.getString("lastName", "")
    val email = body.getString("email", "")
    val password = body.getString("password", "")
    ctx.handleResult(userService.createUser(firstName, lastName, email, password))
  }

  fun patchUser(ctx: RoutingContext) {
    val targetEmail = ctx.pathParam("email")

    val body = ctx.safeBodyAsJson() ?: return
    val newFirstName: String? = body.getString("firstName")
    val newLastName: String? = body.getString("lastName")
    val newEmail: String? = body.getString("email")
    val newPassword: String? = body.getString("password")
    val newDisabled: Boolean? = body.getBoolean("disabled")
    val newRoleNames: JsonArray? = body.getJsonArray("roles")

    val adminNeeded = targetEmail != ctx.getCurrentUserEmail() ||
      newDisabled != null ||
      newRoleNames != null

    if (adminNeeded) {
      if (!ctx.checkCurrentUserHasRole(Role.Admin)) {
        return
      }
    } else {
      if (!ctx.checkCurrentUserHasRole(Role.User)) {
        return
      }
    }

    if (listOf(newFirstName, newLastName, newEmail, newPassword, newDisabled, newRoleNames).all { it == null }) {
      ctx.failValidation(ApiError.Body, "At least one parameter is required for user patch")
      return
    }
    val newRoles = newRoleNames
      ?.mapNotNull { Role.forRoleName(it.toString()) }

    ctx.handleResult(
      userService.patchUser(
        targetEmail,
        newFirstName, newLastName,
        newEmail, newPassword,
        newDisabled, newRoles
      )
    )
  }

  fun getUserTrips(ctx: RoutingContext) {
    val email = ctx.getCurrentUserEmail()
    ctx.handleResult(userService.getUserTrips(email))
  }

  fun getAvailableTripsForUser(ctx: RoutingContext) {
    val description = ctx.queryParam("description").firstOrNull() ?: ""
    ctx.handleResult(userService.getAvailableTrips(description))
  }

  fun postUserTrip(ctx: RoutingContext) {
    val email = ctx.getCurrentUserEmail()
    val tripId = ctx.pathParam("tripId")
    ctx.handleResult(userService.joinTrip(email, tripId.toInt()))
  }

  fun deleteUserTrip(ctx: RoutingContext) {
    val email = ctx.getCurrentUserEmail()
    val tripId = ctx.pathParam("tripId")
    ctx.handleResult(userService.deleteUserTrip(email, tripId.toInt()))
  }
  fun patchImage(ctx: RoutingContext) {
    val targetEmail = ctx.pathParam("email")
    val body = ctx.safeBodyAsJson() ?: return
    val newImage: String? = body.getString("image")
    val adminNeeded = targetEmail != ctx.getCurrentUserEmail()

    if (adminNeeded) {
      if (!ctx.checkCurrentUserHasRole(Role.Admin)) {
        return
      }
    } else {
      if (!ctx.checkCurrentUserHasRole(Role.User)) {
        return
      }
    }
    if (newImage == null) {
      ctx.failValidation(ApiError.Body, "no immage")
    }
    ctx.handleResult(
      userService.patchImage(
        targetEmail,
        newImage
      )
    )
  }
}
