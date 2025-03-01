package indi.gromov.ktor.routes

import indi.gromov.db.SeatRepository
import indi.gromov.models.Seat
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.seatRoutes(seatRepository: SeatRepository) {
    route("/seats") {
        get {
            val seats = seatRepository.allSeats()
            call.respond(seats)
        }
        post {
            val seat = call.receive<Seat>()
            seatRepository.insertSeat(seat)
            call.respond("Seat created")
        }
    }
}