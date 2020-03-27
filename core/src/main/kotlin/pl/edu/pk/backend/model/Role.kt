package pl.edu.pk.backend.model

enum class Role(val id: Int, val roleName: String) {
  Admin(1, "admin"),
  User(2, "user"),
  Guide(3, "guide");

  companion object {
    fun forId(id: Int): Role? {
      return values().firstOrNull { it.id == id }
    }

    fun forRoleName(roleName: String): Role? {
      return values().firstOrNull { it.roleName == roleName.toLowerCase() }
    }
  }
}
