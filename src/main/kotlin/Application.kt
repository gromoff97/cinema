package indi.gromov

import indi.gromov.config.configureRouting
import indi.gromov.config.configureSerialization
import indi.gromov.config.connectDatabase
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectDatabase()
    configureSerialization()
    configureRouting()
}
