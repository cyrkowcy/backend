package pl.edu.pk.backend.service

import io.vertx.core.Future
import pl.edu.pk.backend.model.ApiStatus

class StatusService {
  fun getStatus(): Future<ApiStatus> {
    return Future.succeededFuture(ApiStatus())
  }
}
