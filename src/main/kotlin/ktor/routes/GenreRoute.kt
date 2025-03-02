package indi.gromov.ktor.routes

import indi.gromov.db.GenreRepository
import indi.gromov.models.Genre
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.genreRoutes(genreRepository: GenreRepository) {
    route("/genres") {
        get {
            runCatching {
                genreRepository.allGenres()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val genre = call.receive<Genre>()
                genreRepository.insertGenre(genre)
            }.onSuccess {
                call.respond(mapOf("status" to "success", "message" to "Genre created"))
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }
    }
}