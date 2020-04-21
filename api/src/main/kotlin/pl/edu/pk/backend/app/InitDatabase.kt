package pl.edu.pk.backend.app

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import org.apache.logging.log4j.LogManager
import pl.edu.pk.backend.Services
import pl.edu.pk.backend.model.Role
import pl.edu.pk.backend.model.User

private val logger = LogManager.getLogger("Init database")

class InitDatabase(val services: Services) {

  fun initDatabase() {
    if (Config.stage == "PRO") {
      return
    }
    services.userService.getUsers().onSuccess {
      if (it.isEmpty()) {
        logger.info("Database empty. Adding new rows")
        addUsers()
          .compose { addTickets() }
          .compose { addTrips() }
          .onSuccess { logger.info("Database Initialized") }
      } else {
        logger.info("Database initialized")
      }
    }
  }

  fun addUsers(): Future<User> {
    logger.info("Adding users")
    return services.userService.createUser(
      "Tomek",
      "Nowak",
      "tomek@tomek.pl",
      "123456"
    ).compose {
      services.userService.patchUser(
        "tomek@tomek.pl",
        "Tomek",
        newRoles = listOf(Role.Admin, Role.User)
      )
    }.compose {
      services.userService.createUser(
        "Anna",
        "Kowalska",
        "anna@anna.pl",
        "123456")
        .compose {
          services.userService.patchUser(
            "anna@anna.pl",
            "Anna",
            newRoles = listOf(Role.Guide, Role.User)
          )
        }
    }.compose {
      services.userService.createUser(
        "Grzegorz",
        "Brzęczyszczykiewicz",
        "grzegorz@grzegorz.pl",
        "123456")
    }
  }

  fun addTickets(): Future<JsonObject> {
    logger.info("Adding tickets")
    return services.ticketService.createTicket(
      "anna@anna.pl",
      "Hi! I am new guide."
    ).compose {
      services.ticketService.createTicket(
        "grzegorz@grzegorz.pl",
        "Hi! I am new user."
      )
    }.compose {
      services.ticketService.createComment(
        1,
        "Hi Anna. Nice first ticket!",
        "tomek@tomek.pl",
        true
      )
    }.compose {
      services.ticketService.createTicket(
        "anna@anna.pl",
        "I can't add guide :("
      ).compose {
        services.ticketService.createComment(
          3,
          "Mee too :(",
          "tomek@tomek.pl",
          true
        ).compose {
          services.ticketService.patchTicket(3, closed = true)
        }
      }
    }
  }

  fun addTrips(): Future<JsonObject> {
    logger.info("Adding trips")
    return services.tripService.createTrip(
      "50",
      "Tour around Cracow city.",
      20,
      "2021-05-03T10:15:30+01:00",
      true,
      "Grodzka",
      1,
      2,
      "41°24'12.2\"N 2°10'26.5\"E",
      "41°26'12.2\"N 2°15'26.5\"E",
      "anna@anna.pl"
    ).compose {
      services.tripService.createComment(
        1,
        "Very cheap trip",
        "tomek@tomek.pl"
      )
    }.compose {
      services.tripService.createComment(
        1, "Expensive trip",
        "grzegorz@grzegorz.pl"
      ).compose {
        services.tripService.patchComment(
          1,
          2,
          deleted = true,
          email = "tomek@tomek.pl",
          isAdmin = true
        )
      }.compose {
        services.tripService.createTrip(
          "100",
          "Wisla bank",
          100,
          "2020-05-10T10:15:30+01:00",
          false,
          "Bulwary",
          1,
          2,
          "41°24'12.2\"N 2°10'26.5\"E",
          "41°26'12.2\"N 2°15'26.5\"E",
          "anna@anna.pl"
        ).compose {
          services.tripService.createComment(
            2,
            "Oh, they canceled it. That's sad.",
            "grzegorz@grzegorz.pl"
          )
        }
      }
    }
  }
}
