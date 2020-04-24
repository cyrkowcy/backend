package pl.edu.pk.backend.model

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Ticket(
  val id: Int,
  val author: SensitiveUser,
  val content: String,
  val closed: Boolean,
  val createData: OffsetDateTime,
  val comments: List<TicketComment>
)

data class TicketDto(
  val id: Int,
  val closed: Boolean,
  val author: String,
  val createData: String,
  val content: String
) {
  companion object {
    fun from(ticket: Ticket): TicketDto {
      val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
      return TicketDto(
        ticket.id,
        ticket.closed,
        ticket.author.email,
        ticket.createData.format(formatter),
        ticket.content
      )
    }
  }
}

data class TicketWithComment(
  val ticket: TicketDto,
  val comments: List<TicketCommentDto>
) {
  companion object {
    fun from(ticket: Ticket): TicketWithComment {
      return TicketWithComment(
        TicketDto.from(ticket),
        ticket.comments.map { TicketCommentDto.from(it) }
      )
    }
  }
}
