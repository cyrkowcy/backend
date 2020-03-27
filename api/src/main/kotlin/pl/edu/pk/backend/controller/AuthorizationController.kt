package pl.edu.pk.backend.controller

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.apache.logging.log4j.LogManager
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.service.JwtService
import pl.edu.pk.backend.service.UserService
import pl.edu.pk.backend.util.ApiError

class AuthorizationController(
  private val userService: UserService,
  private val jwtService: JwtService
) : Handler<RoutingContext> {
  private val logger = LogManager.getLogger(this::class.java)

  companion object {
    const val CURRENT_USER_EMAIL = "currentUserEmail"
    const val CURRENT_USER_ROLES = "currentUserRoles"
  }

  override fun handle(ctx: RoutingContext) {
    val authorization = ctx.request().getHeader("authorization")
    if (authorization.isNullOrBlank()) {
      ctx.next()
      return
    }
    val parts = authorization.split(" ")
    if (parts.size != 2 || parts[0] != "Bearer") {
      ctx.failValidation(ApiError.Authorization, "Invalid Authorization header format. Bearer token expected.", 401)
      return
    }
    val token = jwtService.verify(parts[1])
    if (token == null) {
      ctx.failValidation(ApiError.Authorization, "Invalid token", 401)
      return
    }
    val email = token.getClaim("email")
    if (email.isNull) {
      ctx.failValidation(ApiError.Authorization, "Invalid token claims", 401)
      return
    }
    userService.getRoles(email.asString()).setHandler {
      when {
        it.succeeded() -> {
          ctx.put(CURRENT_USER_EMAIL, email.asString())
          ctx.put(CURRENT_USER_ROLES, it.result())
          ctx.next()
        }
        else -> {
          logger.error("Internal authorization error", it.cause())
          ctx.failValidation(ApiError.Authorization, "Internal authorization error", 500)
        }
      }
    }
  }
}

fun RoutingContext.getCurrentUserEmail(): String {
  return get<String>(AuthorizationController.CURRENT_USER_EMAIL) ?: ""
}

fun RoutingContext.checkCurrentUserNone(): Boolean {
  if (getCurrentUserEmail().isNotBlank()) {
    failValidation(ApiError.Authorization, "Already logged in", 400)
    return false
  }
  return true
}

fun RoutingContext.checkCurrentUserHasRole(role: Role): Boolean {
  if (getCurrentUserEmail().isBlank()) {
    failValidation(ApiError.Authorization, "Not logged in", 400)
    return false
  }
  val currentRoles = get<List<Role>>(AuthorizationController.CURRENT_USER_ROLES) ?: emptyList()
  if (!currentRoles.contains(role)) {
    failValidation(ApiError.Authorization, "Insufficient permissions. Role required: ${role.roleName}", 403)
    return false
  }
  return true
}
