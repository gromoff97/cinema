package indi.gromov.ktor.routes

import indi.gromov.db.SessionRepository
import indi.gromov.models.Session
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.sessionRoutes(sessionRepository: SessionRepository) {
    route("/sessions") {
        get {
            val sessions = sessionRepository.allSessions()
            call.respond(sessions)
        }
        post {
            val session = call.receive<Session>()
            sessionRepository.insertSession(session)
            call.respond("Session created")
        }
    }
}