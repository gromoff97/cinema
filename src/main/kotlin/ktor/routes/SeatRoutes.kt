package indi.gromov.ktor.routes

import indi.gromov.db.SeatRepository
import indi.gromov.ktor.requests.SeatCreateRequest
import indi.gromov.models.Seat
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.seatRoutes() {
    route("/seats") {
        get {
            runCatching {
                SeatRepository.allSeats()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val seat = call.receive<SeatCreateRequest>()
                SeatRepository.insertSeat(seat)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}