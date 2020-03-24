package pl.edu.pk.backend.service

import de.mkammerer.argon2.Argon2Factory
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.apache.commons.validator.routines.EmailValidator
import pl.edu.pk.backend.model.Login
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.AuthorizationException
import pl.edu.pk.backend.util.ResourceAlreadyExists
import pl.edu.pk.backend.util.ValidationException

class UserService(
  private val vertx: Vertx,
  private val userRepository: UserRepository,
  private val jwtService: JwtService
) {
  private val passwordRegex = "^.{6,}$".toRegex()
  private val argon2 = Argon2Factory.create()

  fun getUserByEmail(email: String): Future<User> {
    return userRepository.getUserByEmail(email)
      .map { it.toUser() }
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
        hashPassword(password).compose { hashedPassword ->
          userRepository.insertUser(firstName, lastName, email, hashedPassword)
        }
      }
    )
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
}
