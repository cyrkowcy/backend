package pl.edu.pk.backend.util

enum class ApiError(val message: String) {
  Authorization("Invalid authorization"),
  Body("Invalid request body"),
  Query("Invalid query parameters")
}
