package pl.edu.pk.backend.service

import de.mkammerer.argon2.Argon2Factory
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.apache.commons.validator.routines.EmailValidator
import pl.edu.pk.backend.model.Login
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.model.TripDto
import pl.edu.pk.backend.repository.RoleUserRepository
import pl.edu.pk.backend.repository.TripRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.AuthorizationException
import pl.edu.pk.backend.util.ResourceAlreadyExists
import pl.edu.pk.backend.util.ValidationException

class UserService(
  private val vertx: Vertx,
  private val userRepository: UserRepository,
  private val roleUserRepository: RoleUserRepository,
  private val tripRepository: TripRepository,
  private val jwtService: JwtService
) {
  private val passwordRegex = "^.{6,}$".toRegex()
  private val argon2 = Argon2Factory.create()

  fun getUsers(): Future<List<User>> {
    return userRepository.getUsers()
      .compose { users ->
        CompositeFuture.all(users.map { user -> enrichWithUserRoles(user) })
      }.map {
        it.list<User>()
      }
  }

  fun getUserByEmail(email: String): Future<User> {
    return getSensitiveUserByEmail(email)
      .map { it.toUser() }
  }

  fun getSensitiveUserByEmail(email: String): Future<SensitiveUser> {
    return userRepository.getUserByEmail(email)
      .compose { enrichWithUserRoles(it) }
  }

  private fun enrichWithUserRoles(user: SensitiveUser): Future<SensitiveUser> {
    return roleUserRepository.getRoles(user.email)
      .map { user.copy(roles = it) }
  }

  fun loginUser(email: String, password: String): Future<Login> {
    return userRepository.getUserByEmail(email)
      .compose { verifyPassword(it.password, password) }
      .compose(
        { Future.succeededFuture(Login(jwtService.sign(email))) },
        { Future.failedFuture(AuthorizationException("Email or password is invalid")) }
      )
  }

  fun createUser(firstName: String, lastName: String, email: String, password: String): Future<User> {
    if (firstName.isBlank() || lastName.isBlank()) {
      return Future.failedFuture(ValidationException("Invalid name"))
    }
    if (email.isBlank() || !EmailValidator.getInstance().isValid(email)) {
      return Future.failedFuture(ValidationException("Invalid email"))
    }
    if (!passwordRegex.matches(password)) {
      return Future.failedFuture(ValidationException("Invalid password"))
    }
    return getUserByEmail(email).compose(
      { Future.failedFuture<User>(ResourceAlreadyExists("User already exists")) },
      {
        hashPassword(password)
          .compose { hashedPassword ->
            userRepository.insertUser(firstName, lastName, email, hashedPassword)
          }
          .compose { user ->
            roleUserRepository.addRole(email, Role.User).map { user.copy(roles = listOf(Role.User)) }
          }
      }
    )
  }

  fun getRoles(email: String): Future<List<Role>> {
    return roleUserRepository.getRoles(email)
  }

  private fun hashPassword(password: String): Future<String> {
    val promise = Promise.promise<String>()
    vertx.executeBlocking<String>(
      {
        it.complete(argon2.hash(10, 1024, 2, password))
      },
      { res ->
        if (res.succeeded()) {
          promise.complete(res.result())
        } else {
          promise.fail(res.cause())
        }
      })
    return promise.future()
  }

  private fun verifyPassword(hash: String, password: String): Future<Nothing> {
    val promise = Promise.promise<Nothing>()
    vertx.executeBlocking<Boolean>(
      {
        it.complete(argon2.verify(hash, password))
      },
      { res ->
        if (res.succeeded()) {
          if (res.result()) {
            promise.complete()
          } else {
            promise.fail("Password hash validation failed")
          }
        } else {
          promise.fail(res.cause())
        }
      })
    return promise.future()
  }

  fun patchUser(
    email: String,
    newFirstName: String? = null,
    newLastName: String? = null,
    newEmail: String? = null,
    newPassword: String? = null,
    newDisabled: Boolean? = null,
    newRoles: List<Role>? = null
  ): Future<Nothing> {
    if ((newFirstName != null && newFirstName.isBlank()) ||
      (newLastName != null && newLastName.isBlank())
    ) {
      return Future.failedFuture(ValidationException("Invalid name"))
    }
    if (newEmail != null && (newEmail.isBlank() || !EmailValidator.getInstance().isValid(newEmail))) {
      return Future.failedFuture(ValidationException("Invalid email"))
    }
    if (newPassword != null && !passwordRegex.matches(newPassword)) {
      return Future.failedFuture(ValidationException("Invalid password"))
    }
    val startFuture = getUserByEmail(email)
    val hashFuture = if (newPassword != null) {
      startFuture.compose { hashPassword(newPassword) }
    } else {
      startFuture.compose { Future.succeededFuture<String>(null) }
    }
    val finalEmail = newEmail ?: email
    val update = hashFuture.compose { newPasswordHash ->
      userRepository.updateUser(email, newFirstName, newLastName, newEmail, newPasswordHash, newDisabled)
    }
    if (newRoles == null) {
      return update
    }
    return update.compose { getUserByEmail(finalEmail) }
      .compose { roleUserRepository.removeRoles(finalEmail) }
      .compose {
        CompositeFuture.all(newRoles.map { role -> roleUserRepository.addRole(finalEmail, role) })
      }
      .compose { Future.succeededFuture<Nothing>() }
  }

  fun getUserTrips(email: String): Future<List<TripDto>> {
    return tripRepository.getUserTrips(email)
      .map { it.map { TripDto.from(it) } }
  }

  fun getAvailableTrips(description: String): Future<List<TripDto>> {
    return tripRepository.getAvailableTrips(description)
      .map { it.map { TripDto.from(it) } }
  }

  fun joinTrip(email: String, tripId: Int): Future<JsonObject> {
    return tripRepository.getTripByTripId(tripId)
      .compose {
          tripRepository.insertUserTrip(email, tripId)
      }
  }

  fun deleteUserTrip(email: String, tripId: Int): Future<JsonObject> {
    return tripRepository.getTripUserToDelete(email, tripId)
      .compose {
          tripRepository.deleteUserTrip(email, tripId)
      }
  }
}
