package pl.edu.pk.backend.service

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import pl.edu.pk.backend.model.Ticket
import pl.edu.pk.backend.model.TicketCommentDto
import pl.edu.pk.backend.model.TicketComment
import pl.edu.pk.backend.model.TicketDto
import pl.edu.pk.backend.model.TicketWithComment
import pl.edu.pk.backend.repository.TicketCommentRepository
import pl.edu.pk.backend.repository.TicketRepository
import pl.edu.pk.backend.util.AuthorizationException
import pl.edu.pk.backend.util.ValidationException

class TicketService(
  private val ticketRepository: TicketRepository,
  private val userService: UserService,
  private val ticketCommentRepository: TicketCommentRepository
) {

  fun getTickets(): Future<List<TicketDto>> {
    return ticketRepository.getAllTickets()
      .map { it.map { TicketDto.from(it) } }
  }

  fun getTicket(id: Int): Future<TicketWithComment> {
    return ticketRepository.getTicket(id)
      .compose { enrichTicketWithComments(it) }
  }

  fun getTicketWrittenBy(email: String, ticketId: Int): Future<TicketWithComment> {
    return ticketRepository.getTicketByEmail(email, ticketId)
      .compose {
        enrichTicketWithComments(it)
      }
  }

  private fun enrichTicketWithComments(ticket: Ticket): Future<TicketWithComment> {
    return ticketCommentRepository.getComments(ticket.id)
      .compose { enrichCommentsAuthorsWithRoles(it) }
      .map { ticket.copy(comments = it) }
      .map { TicketWithComment.from(it) }
  }

  private fun enrichCommentsAuthorsWithRoles(comments: List<TicketComment>): Future<List<TicketComment>> {
    return CompositeFuture.all(comments
      .map { comment ->
        userService.getRoles(comment.user.email).map { roles ->
          comment.copy(user = comment.user.copy(roles = roles))
        }
      }).map {
      it.list<TicketComment>()
    }
  }

  fun getTicketsWrittenBy(email: String): Future<List<TicketDto>> {
    return ticketRepository.getTicketsByUserEmail(email)
      .map { it.map { TicketDto.from(it) } }
  }

  fun createTicket(email: String, content: String): Future<TicketDto> {
    if (content.isBlank()) {
      return Future.failedFuture(ValidationException("Content is blank."))
    } else if (content.length > 1000) {
      return Future.failedFuture(ValidationException("Content is too long. Max content size 1000"))
    }
    return userService.getSensitiveUserByEmail(email)
      .compose { user -> ticketRepository.insertTicket(user.id, content, email) }
  }

  fun createComment(ticketId: Int, content: String, email: String, isAdmin: Boolean): Future<TicketCommentDto> {
    if (content.isBlank()) {
      return Future.failedFuture(ValidationException("Content is blank."))
    } else if (content.length > 1000) {
      return Future.failedFuture(ValidationException("Content is too long. Max content size 1000"))
    }
    return ticketRepository.getTicket(ticketId)
      .compose { ticket ->
        if (!isAdmin && ticket.author.email != email) {
          Future.failedFuture(AuthorizationException("You don't have permission " +
            "to create comment into ticket: $ticketId"))
        } else {
          userService.getSensitiveUserByEmail(email)
            .compose { user -> ticketCommentRepository.insertComment(ticketId, content, user) }
        }
      }
  }

  fun patchTicket(
    ticketId: Int,
    content: String? = null,
    closed: Boolean? = null,
    email: String? = null
  ): Future<JsonObject> {
    if (content != null && (content.isBlank() || content.length > 1000)) {
      return Future.failedFuture(ValidationException("Content too long or blank."))
    }
    return ticketRepository.update(ticketId, content, closed, email)
  }
}
