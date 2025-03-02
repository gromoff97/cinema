package indi.gromov.ktor.routes

import indi.gromov.db.SessionRepository
import indi.gromov.models.Session
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.sessionRoutes(sessionRepository: SessionRepository) {
    route("/sessions") {
        get {
            runCatching {
                sessionRepository.allSessions()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val session = call.receive<Session>()
                sessionRepository.insertSession(session)
            }.onSuccess {
                call.respond(mapOf("status" to "success", "message" to "Session created"))
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }
    }
}