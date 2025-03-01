package indi.gromov.ktor.routes

import indi.gromov.db.CustomerRepository
import indi.gromov.models.Customer
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.customerRoutes(customerRepository: CustomerRepository) {
    route("/customers") {
        get {
            val customers = customerRepository.allCustomers()
            call.respond(customers)
        }
        post {
            val customer = call.receive<Customer>()
            customerRepository.insertCustomer(customer)
            call.respond("Customer created")
        }
    }
}