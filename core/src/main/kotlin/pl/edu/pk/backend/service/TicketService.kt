package pl.edu.pk.backend.service

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.TicketDto
import pl.edu.pk.backend.repository.TicketRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.ValidationException

class TicketService(private val ticketRepository: TicketRepository, private val userRepository: UserRepository) {
  fun getTickets(): Future<List<TicketDto>> {
    return ticketRepository.getAllTickets().map { it.map { TicketDto.from(it) } }
  }

  fun getTicketsWrittenBy(email: String): Future<List<TicketDto>> {
    return ticketRepository.getTicketsByUserEmail(email).map { it.map { TicketDto.from(it) } }
  }

  fun createTicket(email: String, content: String): Future<JsonObject> {
    if (content.isBlank() || content.length > 1000) {
      return Future.failedFuture(ValidationException("Content too long or blank."))
    }
    return userRepository.getUserByEmail(email).compose { user -> ticketRepository.insertTicket(user.id, content) }
  }

  fun pathTicket(ticketId: Int, content: String?, closed: Boolean?, email: String?): Future<JsonObject> {
    if (content != null && (content.isBlank() || content.length > 1000)) {
      return Future.failedFuture(ValidationException("Content too long or blank."))
    }
    return ticketRepository.update(ticketId, content, closed, email)
  }
}
