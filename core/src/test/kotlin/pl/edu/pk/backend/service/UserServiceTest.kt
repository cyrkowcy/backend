package pl.edu.pk.backend.service

import io.mockk.every
import io.mockk.mockk
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.NoSuchResourceException

@ExtendWith(VertxExtension::class)
class UserServiceTest {
  @Test
  fun `return user`(vertx: Vertx, testContext: VertxTestContext) {
    val service = UserService(vertx, mockUserRepository(), mockk(relaxed = true))

    val result = service.getUserByEmail("foo@example.com")

    assertTrue(result.succeeded())
    assertThat(result.result().email).isEqualTo("foo@example.com")
    testContext.completeNow()
  }

  @Test
  fun `fail for missing user`(vertx: Vertx, testContext: VertxTestContext) {
    val service = UserService(vertx, mockUserRepository(), mockk(relaxed = true))

    val result = service.getUserByEmail("bar")

    assertTrue(result.failed())
    assertThat(result.cause()).isInstanceOf(NoSuchResourceException::class.java)
    testContext.completeNow()
  }

  private fun mockUserRepository(): UserRepository {
    val repo = mockk<UserRepository>()
    every { repo.getUserByEmail(any()) } returns Future.failedFuture(NoSuchResourceException(""))
    every { repo.getUserByEmail(eq("foo@example.com")) } returns Future.succeededFuture(
      SensitiveUser(1, "", "", "foo@example.com", "", disabled = false)
    )
    return repo
  }
}
