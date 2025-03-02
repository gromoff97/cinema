package indi.gromov.ktor.routes

import indi.gromov.db.SessionSeatRepository
import indi.gromov.models.SessionSeat
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.sessionSeatRoutes(sessionSeatRepository: SessionSeatRepository) {
    route("/session-seats") {
        get {
            runCatching {
                sessionSeatRepository.allSessionSeats()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val sessionSeat = call.receive<SessionSeat>()
                sessionSeatRepository.insertSessionSeat(sessionSeat)
            }.onSuccess {
                call.respond(mapOf("status" to "success", "message" to "SessionSeat created"))
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }
    }
}