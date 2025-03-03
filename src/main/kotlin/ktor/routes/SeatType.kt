package indi.gromov.ktor.routes

import indi.gromov.db.SeatTypeRepository
import indi.gromov.ktor.requests.SeatTypeCreateRequest
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.seatTypeRoutes() {
    route("/seat-types") {
        get {
            runCatching {
                SeatTypeRepository.allSeatTypes()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val seatType = call.receive<SeatTypeCreateRequest>()
                SeatTypeRepository.insertSeatType(seatType)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}