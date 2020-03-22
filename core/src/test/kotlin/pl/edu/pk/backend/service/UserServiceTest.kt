package pl.edu.pk.backend.service

import io.mockk.every
import io.mockk.mockk
import io.vertx.core.Future
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.edu.pk.backend.model.SensitiveUser
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.NoSuchResourceException

class UserServiceTest {
  @Test
  fun `return user`() {
    val service = UserService(mockUserRepository())

    val result = service.getUserByEmail("foo@example.com")

    assertTrue(result.succeeded())
    assertThat(result.result().email).isEqualTo("foo@example.com")
  }

  @Test
  fun `fail for missing user`() {
    val service = UserService(mockUserRepository())

    val result = service.getUserByEmail("bar")

    assertTrue(result.failed())
    assertThat(result.cause()).isInstanceOf(NoSuchResourceException::class.java)
  }

  private fun mockUserRepository(): UserRepository {
    val repo = mockk<UserRepository>()
    every { repo.getUserByEmail(any()) } returns Future.failedFuture(NoSuchResourceException(""))
    every { repo.getUserByEmail(eq("foo@example.com")) } returns Future.succeededFuture(
      SensitiveUser(1, "", "", "foo@example.com", "", active = true, disabled = false)
    )
    return repo
  }
}
