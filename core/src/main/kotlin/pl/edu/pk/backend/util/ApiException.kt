package pl.edu.pk.backend.util

open class ApiException(message: String, val httpCode: Int) : Exception(message)
