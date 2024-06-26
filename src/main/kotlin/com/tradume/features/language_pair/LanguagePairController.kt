package com.tradume.features.language_pair

import com.tradume.database.language_pair.LanguagePair
import com.tradume.database.language_pair.LanguagePairDTO
import com.tradume.database.language_pair.generateLanguagePairTitle
import com.tradume.features.language.LanguageController
import com.tradume.utils.hasValidIdValue
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class LanguagePairController(private val call: ApplicationCall) {
    companion object {
        fun checkLanguagePair(
            languageFromId: Int,
            languageToId: Int,
        ): CheckLanguagePairResult {
            val result = CheckLanguagePairResult()

            if (!languageFromId.hasValidIdValue() || !languageToId.hasValidIdValue()) {
                result.apply {
                    isValid = false
                    statusCode = HttpStatusCode.BadRequest
                    message = "Id(s) not specified"
                }
            }

            if (result.isValid && (languageFromId == languageToId)) {
                result.apply {
                    isValid = false
                    statusCode = HttpStatusCode.BadRequest
                    message = "Ids should differ"
                }
            }

            if (result.isValid) {
                val languagePair = fetchLanguagePair(languageFromId, languageToId)

                if (languagePair == null) {
                    result.apply {
                        isValid = false
                        statusCode = HttpStatusCode.NotFound
                        message = "Language pair has undefined language(s) provided"
                    }
                } else {
                    result.languagePairId = languagePair.id
                }
            }

            return result
        }

        fun fetchLanguagePair(
            languageFromId: Int,
            languageToId: Int,
        ): LanguagePairDTO? = LanguagePair.fetchLanguagePair(languageFromId, languageToId)
    }

    suspend fun addLanguagePair() {
        val request = call.receive<AddLanguagePairRequest>()

        val languageFromId: Int = request.languageFromId
        val languageToId: Int = request.languageToId

        val checkIfCanAddLanguagePairResult = checkIfCanAddLanguagePair(
            languageFromId,
            languageToId
        )
        if (checkIfCanAddLanguagePairResult.isValid) {
            val newLanguagePair = addingLanguagePairIfNotExist(
                checkIfCanAddLanguagePairResult.languagePairTitle,
                languageFromId,
                languageToId
            )

            if (newLanguagePair.id > 0) {
                call.respond(newLanguagePair)
            } else {
                call.respond(HttpStatusCode.NotFound, "Language pair is not added")
            }
        } else {
            call.respond(
                checkIfCanAddLanguagePairResult.statusCode,
                checkIfCanAddLanguagePairResult.message
            )
        }
    }

    private fun addingLanguagePairIfNotExist(
        title: String,
        languageFromId: Int,
        languageToId: Int,
    ): LanguagePairDTO {
        val languagePairDTO = LanguagePairDTO(
            title = title,
            languageFromId = languageFromId,
            languageToId = languageToId,
        )
        languagePairDTO.id = LanguagePair.insert(languagePairDTO)

        return languagePairDTO
    }

    private fun checkIfCanAddLanguagePair(
        languageFromId: Int,
        languageToId: Int,
    ): CheckLanguagePairResult {
        val result = CheckLanguagePairResult()

        if (!languageFromId.hasValidIdValue() || !languageToId.hasValidIdValue()) {
            result.apply {
                isValid = false
                statusCode = HttpStatusCode.BadRequest
                message = "Id(s) not specified"
            }
        }

        if (languageFromId == languageToId) {
            result.apply {
                isValid = false
                statusCode = HttpStatusCode.BadRequest
                message = "Ids should differ"
            }
        }

        if (result.isValid) {
            val languageFrom = LanguageController.fetchLanguage(languageFromId)
            val languageTo = LanguageController.fetchLanguage(languageToId)

            if (languageFrom != null && languageTo != null) {
                val languagePairTitle = generateLanguagePairTitle(
                    languageFrom,
                    languageTo
                )
                result.languagePairTitle = languagePairTitle
            } else {
                result.apply {
                    isValid = false
                    statusCode = HttpStatusCode.BadRequest
                    message = "Language pair has undefined language(s) provided"
                }
            }
        }

        return result
    }

    suspend fun fetchLanguagePairs() {
        call.respond(LanguagePair.fetchLanguagePairs())
    }
}
