package indi.gromov.ktor.routes

import indi.gromov.db.CustomerRepository
import indi.gromov.ktor.requests.CustomerCreateRequest
import indi.gromov.utils.transaction.extensions.toModel
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.customerRoutes() {
    route("/customers") {
        get {
            runCatching {
                CustomerRepository.allCustomers()
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(InternalServerError, mapOf("status" to "error", "message" to it.message))
            }
        }

        post {
            runCatching {
                val customer = call.receive<CustomerCreateRequest>()
                CustomerRepository.insertCustomer(customer)
            }.onSuccess {
                call.respond(it.toModel())
            }.onFailure {
                call.respond(InternalServerError, mapOf("details" to it.message))
            }
        }
    }
}