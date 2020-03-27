package pl.edu.pk.backend.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.Test
import pl.edu.pk.backend.model.Login
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.service.UserService

class UserControllerTest {
  @Test
  fun `bad request for missing email`() {
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { ctx.queryParam("email") } returns listOf()
    every { ctx.get<String>("currentUserEmail") } returns ""

    controller.getUser(ctx)

    verify { ctx.response().setStatusCode(400) }
  }

  @Test
  fun `return user`() {
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { service.getUserByEmail(any()) } returns
      Future.succeededFuture(User("", "", "", false, listOf(Role.User)))
    every { ctx.queryParam("email") } returns listOf("test@example.com")
    every { ctx.get<String>("currentUserEmail") } returns "test@example.com"
    every { ctx.get<List<Role>>("currentUserRoles") } returns listOf(Role.User)

    controller.getUser(ctx)

    verify { ctx.response().setStatusCode(200) }
  }

  @Test
  fun `login user`() {
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { service.loginUser(any(), any()) } returns Future.succeededFuture(Login(""))
    every { ctx.bodyAsJson } returns JsonObject(mapOf("email" to "foo@foo.com", "password" to "bar"))
    every { ctx.get<String>("currentUserEmail") } returns ""

    controller.postLogin(ctx)

    verify { service.loginUser("foo@foo.com", "bar") }
    verify { ctx.response().setStatusCode(200) }
  }

  @Test
  fun `register user`() {
    val ctx = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { service.createUser(any(), any(), any(), any()) } returns
      Future.succeededFuture(User("", "", "", false, listOf(Role.User)))
    every { ctx.get<String>("currentUserEmail") } returns null
    every { ctx.bodyAsJson } returns JsonObject(
      mapOf(
        "firstName" to "Foo",
        "lastName" to "Bar",
        "email" to "foo@foo.com",
        "password" to "bar"
      )
    )

    controller.postUser(ctx)

    verify { service.createUser("Foo", "Bar", "foo@foo.com", "bar") }
    verify { ctx.response().setStatusCode(200) }
  }
}
