package com.tradume

import com.tradume.features.language.configureLanguageRouting
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.tradume.plugins.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/tradume",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "nU5sP8Zc"
    )

    embeddedServer(
        CIO,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureLanguageRouting()
    configureSerialization()
}
