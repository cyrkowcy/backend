package pl.edu.pk.backend.model

data class User(
  val firstName: String,
  val lastName: String,
  val email: String,
  val disabled: Boolean,
  val roles: List<Role>
)

data class SensitiveUser(
  val id: Int,
  val firstName: String,
  val lastName: String,
  val email: String,
  val password: String,
  val disabled: Boolean,
  val roles: List<Role>
) {
  fun toUser(): User {
    return User(firstName, lastName, email, disabled, roles)
  }
}
