package pl.edu.pk.backend.model

import java.time.OffsetDateTime

class TicketComment(
  val id: Int,
  val content: String,
  val user: SensitiveUser,
  val createData: OffsetDateTime
)

class TicketCommentDto(
  val content: String,
  val author: String
) {
  companion object {
    fun from(ticketComment: TicketComment): TicketCommentDto {
      return TicketCommentDto(
        ticketComment.content,
        ticketComment.user.email
      )
    }
  }
}
