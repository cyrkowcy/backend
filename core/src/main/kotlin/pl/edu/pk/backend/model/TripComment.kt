package pl.edu.pk.backend.model

class TripComment(
  val id: Int,
  val user: SensitiveUser,
  val content: String,
  val deleted: Boolean
)

class TripCommentDto(
  val content: String,
  val author: User
) {
  companion object {
    fun from(tripComment: TripComment): TripCommentDto {
      return TripCommentDto(
        tripComment.content,
        tripComment.user.toUser()
      )
    }
  }
}
