package com.tradume

import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.tradume.plugins.*
import io.ktor.server.application.*

fun main() {
    embeddedServer(
        CIO,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
}
