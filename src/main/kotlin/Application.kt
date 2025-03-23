package dev.haas

import configureDatabase
import dev.haas.routes.configureAuthRouting
import dev.haas.routes.configureHabitRoutes
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureFrameworks()
    configureSerialization()
    configureDatabase()
    configureHTTP()
    configureSecurity()
    configureHabitRoutes()
    configureAuthRouting()
}
