package pl.edu.pk.backend.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.Test
import pl.edu.pk.backend.model.Login
import pl.edu.pk.backend.model.User
import pl.edu.pk.backend.service.UserService

class UserControllerTest {
  @Test
  fun `bad request for missing email`() {
    val context = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { context.queryParam("email") } returns listOf()

    controller.getUser(context)

    verify { context.response().setStatusCode(400) }
  }

  @Test
  fun `return user`() {
    val context = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { service.getUserByEmail(any()) } returns Future.succeededFuture(User("", "", "", false))
    every { context.queryParam("email") } returns listOf("test@example.com")

    controller.getUser(context)

    verify { context.response().setStatusCode(200) }
  }

  @Test
  fun `login user`() {
    val context = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { service.loginUser(any(), any()) } returns Future.succeededFuture(Login(""))
    every { context.bodyAsJson } returns JsonObject(mapOf("email" to "foo@foo.com", "password" to "bar"))

    controller.postLogin(context)

    verify { service.loginUser("foo@foo.com", "bar") }
    verify { context.response().setStatusCode(200) }
  }

  @Test
  fun `register user`() {
    val context = mockk<RoutingContext>(relaxed = true)
    val service = mockk<UserService>(relaxed = true)
    val controller = UserController(service)
    every { service.createUser(any(), any(), any(), any()) } returns Future.succeededFuture(User("", "", "", false))
    every { context.bodyAsJson } returns JsonObject(
      mapOf(
        "firstName" to "Foo",
        "lastName" to "Bar",
        "email" to "foo@foo.com",
        "password" to "bar"
      )
    )

    controller.postUser(context)

    verify { service.createUser("Foo", "Bar", "foo@foo.com", "bar") }
    verify { context.response().setStatusCode(200) }
  }
}
