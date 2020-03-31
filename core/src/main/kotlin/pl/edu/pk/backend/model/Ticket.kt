package pl.edu.pk.backend.model

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Ticket(
  val id: Int,
  val author: SensitiveUser,
  val content: String,
  val closed: Boolean,
  val createData: OffsetDateTime
)

data class TicketDto(
  val id: Int,
  val closed: Boolean,
  val author: String,
  val createData: String
) {
  companion object {
    fun from(ticket: Ticket): TicketDto {
      val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
      return TicketDto(
        ticket.id,
        ticket.closed,
        "user_" + ticket.author.id,
        ticket.createData.format(formatter))
    }
  }
}
