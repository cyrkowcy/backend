package pl.edu.pk.backend.service

import io.vertx.core.Future
import pl.edu.pk.backend.model.ApiStatus
import kotlin.system.exitProcess

class StatusService {
  fun getStatus(): Future<ApiStatus> {
    return Future.succeededFuture(ApiStatus())
  }
  fun shutdown(): Future<ApiStatus> {
    exitProcess(0)
  }
}
