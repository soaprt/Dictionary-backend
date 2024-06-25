package com.tradume.features.language_pair

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLanguagePairRouting() {
    routing {
        get("/language-pairs") {
            with(LanguagePairController(call)) {
                fetchLanguagePairs()
            }
        }

        post("/language-pair") {
            with(LanguagePairController(call)) {
                addLanguagePair()
            }
        }
    }
}
