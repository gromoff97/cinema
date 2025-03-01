package indi.gromov.ktor.routes

import indi.gromov.db.SeatTypeRepository
import indi.gromov.models.SeatType
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.seatTypeRoutes(seatTypeRepository: SeatTypeRepository) {
    route("/seat-types") {
        get {
            val seatTypes = seatTypeRepository.allSeatTypes()
            call.respond(seatTypes)
        }
        post {
            val seatType = call.receive<SeatType>()
            seatTypeRepository.insertSeatType(seatType)
            call.respond("SeatType created")
        }
    }
}