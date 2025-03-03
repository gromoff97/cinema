package indi.gromov.config

import indi.gromov.ktor.routes.bookingRoutes
import indi.gromov.ktor.routes.customerRoutes
import indi.gromov.ktor.routes.filmRoutes
import indi.gromov.ktor.routes.genreRoutes
import indi.gromov.ktor.routes.hallRoutes
import indi.gromov.ktor.routes.seatRoutes
import indi.gromov.ktor.routes.seatTypeRoutes
import indi.gromov.ktor.routes.sessionRoutes
import indi.gromov.ktor.routes.sessionSeatRoutes
import indi.gromov.ktor.routes.staticRoute
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        exception<RequestValidationException> { call, cause ->
            call.respond(BadRequest, mapOf("status" to "error", "message" to cause.message))
        }
    }

    routing {
        staticRoute()
        bookingRoutes()
        customerRoutes()
        filmRoutes()
        genreRoutes()
        hallRoutes()
        seatRoutes()
        seatTypeRoutes()
        sessionRoutes()
        sessionSeatRoutes()
    }
}
