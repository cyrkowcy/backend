package pl.edu.pk.backend.model

data class User(
  val firstName: String,
  val lastName: String,
  val email: String,
  val disabled: Boolean
)

data class SensitiveUser(
  val id: Int,
  val firstName: String,
  val lastName: String,
  val email: String,
  val password: String,
  val active: Boolean,
  val disabled: Boolean
) {
  fun toUser(): User {
    return User(firstName, lastName, email, disabled)
  }
}
