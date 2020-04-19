package pl.edu.pk.backend.app

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.Services
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.model.User

class InitDatabase(val services: Services) {

  fun initDatabase() {
    services.userService.getUsers().onSuccess {
      if (it.isEmpty()) {
        addUsers().compose {
          addTickets()
        }
      }
    }
  }

  fun addUsers(): Future<User> {
    return services.userService.createUser("Tomek", "Nowak", "tomek@tomek.pl", "123456")
      .compose {
        services.userService.patchUser("tomek@tomek.pl", "Tomek",
          null, null, null, null, listOf(Role.Admin, Role.User))
      }.compose {
        services.userService.createUser("Anna", "Kowalska", "anna@anna.pl", "123456")
          .compose {
            services.userService.patchUser("anna@anna.pl", "Anna",
              null, null, null, null, listOf(Role.Guide, Role.User))
          }
      }.compose {
        services.userService.createUser("Grzegorz",
          "Brzęczyszczykiewicz", "grzegorz@grzegorz.pl", "123456")
      }
  }

  fun addTickets(): Future<JsonObject> {
    return services.ticketService.createTicket("anna@anna.pl", "Hi! I am new guide.")
      .compose {
        services.ticketService.createTicket("grzegorz@grzegorz.pl", "Hi! I am new user.")
      }.compose {
        services.ticketService.createComment(1, "Hi Anna. Nice first ticket!",
          "tomek@tomek.pl", true)
      }
  }
}
