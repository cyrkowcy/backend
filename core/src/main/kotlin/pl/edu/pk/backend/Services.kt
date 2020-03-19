package pl.edu.pk.backend

import pl.edu.pk.backend.service.StatusService

class Services {
  val statusService by lazy {
    StatusService()
  }
}
