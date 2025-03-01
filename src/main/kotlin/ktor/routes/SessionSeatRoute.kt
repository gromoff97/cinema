package indi.gromov.ktor.routes

import indi.gromov.db.SessionSeatRepository
import indi.gromov.models.SessionSeat
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.sessionSeatRoutes(sessionSeatRepository: SessionSeatRepository) {
    route("/session-seats") {
        get {
            val sessionSeats = sessionSeatRepository.allSessionSeats()
            call.respond(sessionSeats)
        }
        post {
            val sessionSeat = call.receive<SessionSeat>()
            sessionSeatRepository.insertSessionSeat(sessionSeat)
            call.respond("SessionSeat created")
        }
    }
}