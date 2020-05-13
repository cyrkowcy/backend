package pl.edu.pk.backend.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import java.time.ZonedDateTime
import java.util.Date

class JwtService(secret: String) {
  private val algorithm = Algorithm.HMAC256(secret)
  private val verifier: JWTVerifier = JWT.require(algorithm).build()

  fun sign(email: String): String = JWT.create()
    .withClaim("email", email)
    .withExpiresAt(Date.from(ZonedDateTime.now().plusDays(365).toInstant()))
    .sign(algorithm)

  fun verify(token: String): DecodedJWT? {
    return try {
      verifier.verify(token)
    } catch (e: JWTVerificationException) {
      null
    }
  }
}
