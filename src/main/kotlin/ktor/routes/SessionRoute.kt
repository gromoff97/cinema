package indi.gromov.ktor.routes

import indi.gromov.db.SessionRepository
import indi.gromov.ktor.requests.SessionCreateRequest
import indi.gromov.models.Session
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.sessionRoutes() {
    route("/sessions") {
        get {
            runCatching {
                SessionRepository.allSessions()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val session = call.receive<SessionCreateRequest>()
                SessionRepository.insertSession(session)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}