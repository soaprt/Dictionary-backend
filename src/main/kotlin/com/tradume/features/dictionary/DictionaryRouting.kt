package com.tradume.features.dictionary

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDictionaryRouting() {
    routing {
        get("/dictionary") {
            with(DictionaryController(call)) {
                fetchWordsWithTranslation()
            }
        }
    }
}
