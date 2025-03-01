package indi.gromov.ktor.routes

import indi.gromov.db.BookingRepository
import indi.gromov.models.Booking
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond

fun Route.bookingRoutes(bookingRepository: BookingRepository) {
    route("/bookings") {
        get {
            val bookings = bookingRepository.allBookings()
            call.respond(bookings)
        }
        post {
            val booking = call.receive<Booking>()
            bookingRepository.insertBooking(booking)
            call.respond("Booking created")
        }
    }
}