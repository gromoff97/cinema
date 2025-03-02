package indi.gromov.ktor.routes

import indi.gromov.db.SeatRepository
import indi.gromov.models.Seat
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.seatRoutes(seatRepository: SeatRepository) {
    route("/seats") {
        get {
            runCatching {
                seatRepository.allSeats()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val seat = call.receive<Seat>()
                seatRepository.insertSeat(seat)
            }.onSuccess {
                call.respond(mapOf("status" to "success", "message" to "Seat created"))
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }
    }
}