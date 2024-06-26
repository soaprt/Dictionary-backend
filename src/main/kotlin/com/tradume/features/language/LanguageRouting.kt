package com.tradume.features.language

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLanguageRouting() {
    routing {
        get("/languages") {
            with(LanguageController(call)) {
                fetchLanguages()
            }
        }

        post("/language") {
            with(LanguageController(call)) {
                addLanguage()
            }
        }
    }
}
