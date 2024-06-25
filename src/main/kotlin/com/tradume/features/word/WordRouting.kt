package com.tradume.features.word

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureWordRouting() {
    routing {
        get("/words") {
            with(WordController(call)) {
                fetchWords()
            }
        }

        post("/word") {
            with(WordController(call)) {
                addWord()
            }
        }
    }
}
