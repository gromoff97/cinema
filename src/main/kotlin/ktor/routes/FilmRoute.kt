package indi.gromov.ktor.routes

import indi.gromov.db.FilmRepository
import indi.gromov.ktor.requests.FilmCreateRequest
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.filmRoutes(filmRepository: FilmRepository) {
    route("/films") {
        get {
            runCatching {
                filmRepository.allFilms()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val film = call.receive<FilmCreateRequest>()
                filmRepository.insertFilm(film)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}