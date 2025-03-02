package indi.gromov.config

import indi.gromov.db.BookingRepository
import indi.gromov.db.CustomerRepository
import indi.gromov.db.FilmRepository
import indi.gromov.db.GenreRepository
import indi.gromov.db.HallRepository
import indi.gromov.db.SeatRepository
import indi.gromov.db.SeatTypeRepository
import indi.gromov.db.SessionRepository
import indi.gromov.db.SessionSeatRepository
import indi.gromov.ktor.routes.bookingRoutes
import indi.gromov.ktor.routes.customerRoutes
import indi.gromov.ktor.routes.filmRoutes
import indi.gromov.ktor.routes.genreRoutes
import indi.gromov.ktor.routes.hallRoutes
import indi.gromov.ktor.routes.seatRoutes
import indi.gromov.ktor.routes.seatTypeRoutes
import indi.gromov.ktor.routes.sessionRoutes
import indi.gromov.ktor.routes.sessionSeatRoutes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.response.respond

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/show") {
            val htmlContent = this::class.java.classLoader.getResource("static/show.html")?.readText()
            if (htmlContent != null) {
                call.respondText(htmlContent, ContentType.Text.Html)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }

        // Маршрут для /admin
        get("/admin") {
            val htmlContent = this::class.java.classLoader.getResource("static/admin.html")?.readText()
            if (htmlContent != null) {
                call.respondText(htmlContent, ContentType.Text.Html)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }

        staticResources("/", "static")

        bookingRoutes(BookingRepository())
        customerRoutes(CustomerRepository())
        filmRoutes(FilmRepository())
        genreRoutes(GenreRepository())
        hallRoutes(HallRepository())
        seatRoutes(SeatRepository())
        seatTypeRoutes(SeatTypeRepository())
        sessionRoutes(SessionRepository())
        sessionSeatRoutes(SessionSeatRepository())
    }
}
