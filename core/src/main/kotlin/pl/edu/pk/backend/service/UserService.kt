package pl.edu.pk.backend.service

import io.vertx.core.Future
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.repository.UserRepository

class UserService(private val userRepository: UserRepository) {
  fun getUserByEmail(email: String): Future<User> {
    return userRepository.getUserByEmail(email)
      .map { it.toUser() }
  }
}
