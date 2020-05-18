package pl.edu.pk.backend.util

class NoSuchResourceException(message: String) : ApiException(message, 404)
