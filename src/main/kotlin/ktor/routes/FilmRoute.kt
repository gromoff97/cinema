package indi.gromov.ktor.routes

import indi.gromov.db.FilmRepository
import indi.gromov.models.Film
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.filmRoutes(filmRepository: FilmRepository) {
    route("/films") {
        get {
            val films = filmRepository.allFilms()
            call.respond(films)
        }
        post {
            val film = call.receive<Film>()
            filmRepository.insertFilm(film)
            call.respond("Film created")
        }
    }
}