package indi.gromov.ktor.routes

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.staticRoute() {
    get("/show") {
        val htmlContent = this::class.java.classLoader.getResource("static/show.html")?.readText()
        if (htmlContent != null) {
            call.respondText(htmlContent, ContentType.Text.Html)
        } else {
            call.respond(HttpStatusCode.NotFound, "File not found")
        }
    }

    get("/admin") {
        val htmlContent = this::class.java.classLoader.getResource("static/admin.html")?.readText()
        if (htmlContent != null) {
            call.respondText(htmlContent, ContentType.Text.Html)
        } else {
            call.respond(HttpStatusCode.NotFound, "File not found")
        }
    }

    staticResources("/", "static")
}