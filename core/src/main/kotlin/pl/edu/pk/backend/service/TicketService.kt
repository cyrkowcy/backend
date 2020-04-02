package pl.edu.pk.backend.service

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.Ticket
import pl.edu.pk.backend.model.TicketDto
import pl.edu.pk.backend.model.TicketWithComment
import pl.edu.pk.backend.repository.TicketCommentRepository
import pl.edu.pk.backend.repository.TicketRepository
import pl.edu.pk.backend.repository.UserRepository
import pl.edu.pk.backend.util.AuthorizationException
import pl.edu.pk.backend.util.ValidationException

class TicketService(
  private val ticketRepository: TicketRepository,
  private val userRepository: UserRepository,
  private val ticketCommentRepository: TicketCommentRepository) {

  fun getTickets(): Future<List<TicketDto>> {
    return ticketRepository.getAllTickets()
      .map { it.map { TicketDto.from(it) } }
  }

  fun getTicket(id: Int): Future<TicketWithComment> {
    return ticketRepository.getTicket(id)
      .compose { enrichTicketWithComment(it) }
  }

  fun getTicketWrittenBy(email: String, ticketId: Int): Future<TicketWithComment> {
    return ticketRepository.getTicketByEmail(email, ticketId)
      .compose {
        enrichTicketWithComment(it)}
  }

  private fun enrichTicketWithComment(ticket: Ticket): Future<TicketWithComment> {
    return ticketCommentRepository.getComments(ticket.id)
      .map { ticket.copy(comments = it) }
      .map { TicketWithComment.from(it) }
  }

  fun getTicketsWrittenBy(email: String): Future<List<TicketDto>> {
    return ticketRepository.getTicketsByUserEmail(email)
      .map { it.map { TicketDto.from(it) } }
  }

  fun createTicket(email: String, content: String): Future<JsonObject> {
    if (content.isBlank()) {
      return Future.failedFuture(ValidationException("Content is blank."))
    } else if (content.length > 1000) {
      return Future.failedFuture(ValidationException("Content is too long. Max content size 1000"))
    }
    return userRepository.getUserByEmail(email)
      .compose { user -> ticketRepository.insertTicket(user.id, content) }
  }

  fun createComment(ticketId: Int, content: String, email: String, isAdmin: Boolean): Future<JsonObject> {
    if (content.isBlank()) {
      return Future.failedFuture(ValidationException("Content is blank."))
    } else if (content.length > 1000) {
      return Future.failedFuture(ValidationException("Content is too long. Max content size 1000"))
    }
    return ticketRepository.getTicket(ticketId)
      .compose { ticket ->
        if (!isAdmin && ticket.author.email != email) {
          Future.failedFuture(AuthorizationException("You don't have permission to create comment into ticket: $ticketId"))
        } else {
          userRepository.getUserByEmail(email).compose { ticketCommentRepository.insertComment(ticketId, content, it.id) }
        }
      }
  }

  fun patchTicket(ticketId: Int, content: String?, closed: Boolean?, email: String?): Future<JsonObject> {
    if (content != null && (content.isBlank() || content.length > 1000)) {
      return Future.failedFuture(ValidationException("Content too long or blank."))
    }
    return ticketRepository.update(ticketId, content, closed, email)
  }
}
