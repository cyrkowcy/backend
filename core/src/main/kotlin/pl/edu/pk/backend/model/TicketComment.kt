package pl.edu.pk.backend.model

import java.time.OffsetDateTime

data class TicketComment(
  val id: Int,
  val content: String,
  val user: SensitiveUser,
  val createDate: OffsetDateTime
)

data class TicketCommentDto(
  val content: String,
  val author: User
) {
  companion object {
    fun from(ticketComment: TicketComment): TicketCommentDto {
      return TicketCommentDto(
        ticketComment.content,
        ticketComment.user.toUser()
      )
    }
  }
}
