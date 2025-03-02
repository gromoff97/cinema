package indi.gromov.ktor.routes

import indi.gromov.db.HallRepository
import indi.gromov.ktor.requests.HallCreateRequest
import indi.gromov.models.Hall
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.hallRoutes(hallRepository: HallRepository) {
    route("/halls") {
        get {
            runCatching {
                hallRepository.allHalls()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val hall = call.receive<HallCreateRequest>()
                hallRepository.insertHall(hall)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}