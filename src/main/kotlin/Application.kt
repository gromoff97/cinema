package indi.gromov

import indi.gromov.db.customer.PostgresCustomerRepository
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val customerRepository = PostgresCustomerRepository()

    connectDatabase()
    configureSerialization(customerRepository)
    configureRouting()
}
