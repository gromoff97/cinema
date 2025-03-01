package indi.gromov.ktor.routes

import indi.gromov.db.HallRepository
import indi.gromov.models.Hall
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.hallRoutes(hallRepository: HallRepository) {
    route("/halls") {
        get {
            val halls = hallRepository.allHalls()
            call.respond(halls)
        }
        post {
            val hall = call.receive<Hall>()
            hallRepository.insertHall(hall)
            call.respond("Hall created")
        }
    }
}