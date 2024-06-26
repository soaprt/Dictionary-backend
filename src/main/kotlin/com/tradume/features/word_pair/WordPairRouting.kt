package com.tradume.features.word_pair

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureWordPairRouting() {
    routing {
        get("/word-pairs") {
            with(WordPairController(call)) {
                fetchAllWordPairs()
            }
        }

        post("/word-pair") {
            with(WordPairController(call)) {
                addWordPair()
            }
        }
    }
}
