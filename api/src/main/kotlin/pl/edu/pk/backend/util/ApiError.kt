package pl.edu.pk.backend.util

enum class ApiError(val message: String) {
  Body("Invalid request body"),
  Query("Invalid query parameters")
}
