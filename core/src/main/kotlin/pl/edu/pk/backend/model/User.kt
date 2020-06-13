package pl.edu.pk.backend.model

data class User(
  val firstName: String,
  val lastName: String,
  val email: String,
  val disabled: Boolean,
  val roles: List<Role>,
  val createDate: String
)

data class SensitiveUser(
  val id: Int,
  val firstName: String,
  val lastName: String,
  val email: String,
  val password: String,
  val disabled: Boolean,
  val roles: List<Role>,
  val createDate: String,
  val image: String
) {
  fun toUser(): User {
    return User(firstName, lastName, email, disabled, roles, createDate)
  }
}
