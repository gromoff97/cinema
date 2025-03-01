package indi.gromov.ktor.routes

import indi.gromov.db.GenreRepository
import indi.gromov.models.Genre
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.genreRoutes(genreRepository: GenreRepository) {
    route("/genres") {
        get {
            val genres = genreRepository.allGenres()
            call.respond(genres)
        }
        post {
            val genre = call.receive<Genre>()
            genreRepository.insertGenre(genre)
            call.respond("Genre created")
        }
    }
}