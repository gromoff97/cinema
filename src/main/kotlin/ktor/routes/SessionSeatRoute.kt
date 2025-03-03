package indi.gromov.ktor.routes

import indi.gromov.db.SessionSeatRepository
import indi.gromov.ktor.requests.SessionSeatCreateRequest
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.sessionSeatRoutes() {
    route("/session-seats") {
        get {
            runCatching {
                SessionSeatRepository.allSessionSeats()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val sessionSeat = call.receive<SessionSeatCreateRequest>()
                SessionSeatRepository.insertSessionSeat(sessionSeat)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}