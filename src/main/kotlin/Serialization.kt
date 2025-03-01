package indi.gromov

import indi.gromov.db.customer.CustomerRepository
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureSerialization(repository: CustomerRepository) {
    install(ContentNegotiation) { json() }

    routing {
        route("/customers") {
            get {
                val customers = repository.allCustomers()
                call.respond(customers)
            }
        }
    }
}
