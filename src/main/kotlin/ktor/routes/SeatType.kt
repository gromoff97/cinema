package indi.gromov.ktor.routes

import indi.gromov.db.SeatTypeRepository
import indi.gromov.ktor.requests.SeatTypeCreateRequest
import indi.gromov.models.SeatType
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.seatTypeRoutes(seatTypeRepository: SeatTypeRepository) {
    route("/seat-types") {
        get {
            runCatching {
                seatTypeRepository.allSeatTypes()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val seatType = call.receive<SeatTypeCreateRequest>()
                seatTypeRepository.insertSeatType(seatType)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}