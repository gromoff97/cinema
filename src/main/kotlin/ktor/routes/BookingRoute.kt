package indi.gromov.ktor.routes

import indi.gromov.db.BookingRepository
import indi.gromov.models.Booking
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.bookingRoutes(bookingRepository: BookingRepository) {
    route("/bookings") {
        get {
            runCatching {
                bookingRepository.allBookings()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val booking = call.receive<Booking>()
                bookingRepository.insertBooking(booking)
            }.onSuccess {
                call.respond(mapOf("status" to "success", "message" to "Booking created"))
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }
    }
}