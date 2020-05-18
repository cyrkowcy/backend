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
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.repository.RoleUserRepository
import pl.edu.pk.backend.repository.TripRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.NoSuchResourceException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@ExtendWith(VertxExtension::class)
class UserServiceTest {
  @Test
  fun `return user`(vertx: Vertx, testContext: VertxTestContext) {
    val service = UserService(vertx,
      mockUserRepository(),
      mockRoleUserRepository(),
      mockTripRepository(),
      mockk(relaxed = true))

    val result = service.getUserByEmail("foo@example.com")

    assertTrue(result.succeeded())
    assertThat(result.result().email).isEqualTo("foo@example.com")
    testContext.completeNow()
  }

  @Test
  fun `fail for missing user`(vertx: Vertx, testContext: VertxTestContext) {
    val service = UserService(vertx,
      mockUserRepository(),
      mockk(relaxed = true),
      mockTripRepository(),
      mockk(relaxed = true))

    val result = service.getUserByEmail("bar")

    assertTrue(result.failed())
    assertThat(result.cause()).isInstanceOf(NoSuchResourceException::class.java)
    testContext.completeNow()
  }

  private fun mockUserRepository(): UserRepository {
    val repo = mockk<UserRepository>()
    every { repo.getUserByEmail(any()) } returns Future.failedFuture(NoSuchResourceException(""))
    every { repo.getUserByEmail(eq("foo@example.com")) } returns Future.succeededFuture(
      SensitiveUser(1, "", "", "foo@example.com", "", false, listOf(Role.User),
        OffsetDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
    )
    return repo
  }

  private fun mockRoleUserRepository(): RoleUserRepository {
    val repo = mockk<RoleUserRepository>()
    every { repo.getRoles(any()) } returns Future.succeededFuture(listOf(Role.User))
    return repo
  }

  private fun mockTripRepository(): TripRepository {
    return mockk()
  }
}
