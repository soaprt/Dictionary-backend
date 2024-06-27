package com.tradume.features.dictionary

import com.tradume.features.language_pair.CheckLanguagePairResult
import com.tradume.features.language_pair.LanguagePairController
import com.tradume.features.toFetchMultipleDataResult
import com.tradume.features.word.WordController
import com.tradume.features.word_pair.WordPairController
import com.tradume.utils.CheckDataResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class DictionaryController(private val call: ApplicationCall) {
    suspend fun fetchWordsWithTranslation() {
        val requestParameters = call.request.queryParameters
        val languageFromId = requestParameters["languageFromId"]?.toIntOrNull() ?: 0
        val languageToId = requestParameters["languageToId"]?.toIntOrNull() ?: 0

        val checkIfCanFetchWords = checkIfCanFetchWords(
            languageFromId,
            languageToId,
        )

        if (checkIfCanFetchWords.isValid) {
            val languagePairId = (checkIfCanFetchWords as CheckLanguagePairResult).languagePairId
            val wordPairs = WordPairController.fetchWordPairs(
                languagePairId
            )

            if (wordPairs.isNotEmpty()) {
                val resultMap: MutableMap<String, MutableList<String>> = mutableMapOf()

                for (wordPair in wordPairs) {
                    val key = WordController.fetchWord(
                        wordPair.wordFromId
                    )?.word
                    val value = WordController.fetchWord(
                        wordPair.wordToId
                    )

                    if (key != null && value != null) {
                        if (resultMap.containsKey(key)) {
                            resultMap[key]?.also {
                                it.add(value.word)
                            }
                        } else {
                            resultMap[key] = mutableListOf(value.word)
                        }
                    }
                }

                call.respond(
                    toFetchMultipleDataResult(
                        resultMap.entries.map { toWordWithTranslation(it.key, it.value) }
                    )
                )
            } else {
                call.respond(
                    toFetchMultipleDataResult(
                        emptyList<String>()
                    )
                )
            }
        } else {
            if (checkIfCanFetchWords.statusCode == HttpStatusCode.NoContent) {
                call.respond(
                    emptyList<String>()
                )
            } else {
                call.respond(
                    checkIfCanFetchWords.statusCode,
                    checkIfCanFetchWords.message,
                )
            }
        }
    }

    private fun checkIfCanFetchWords(
        languageFromId: Int,
        languageToId: Int,
    ): CheckDataResult {
        val result = LanguagePairController.checkLanguagePair(languageFromId, languageToId)

        if (!result.isValid) {
            result.apply {
                isValid = false
                statusCode = HttpStatusCode.NoContent
            }
        }

        return result
    }
}
