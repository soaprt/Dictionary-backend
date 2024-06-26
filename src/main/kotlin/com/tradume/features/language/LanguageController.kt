package com.tradume.features.language

import com.tradume.database.language.Language
import com.tradume.database.language.LanguageDTO
import com.tradume.utils.CheckDataResult
import com.tradume.utils.hasValue
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class LanguageController(private val call: ApplicationCall) {
    companion object {
        fun addingLanguageIfNotExist(
            locale: String,
            title: String,
        ): LanguageDTO {
            val languageDTO = LanguageDTO(
                locale = locale,
                title = title,
            )

            languageDTO.id = Language.insert(languageDTO)

            return languageDTO
        }

        private fun checkIfCanAddLanguage(
            locale: String,
            title: String,
        ): CheckDataResult {
            val result = CheckDataResult()

            if (!title.hasValue() || !locale.hasValue()) {
                result.apply {
                    isValid = false
                    statusCode = HttpStatusCode.BadRequest
                    message = "Required value(s) not specified"
                }
            }

            return result
        }

        fun fetchLanguage(
            id: Int = 0,
            title: String = "",
            locale: String = "",
        ): LanguageDTO? = Language.fetchLanguage(
            id = id, title = title, locale = locale
        )

        fun hasLanguage(
            id: Int = 0,
            title: String = "",
            locale: String = "",
        ): Boolean {
            val languageDTO = fetchLanguage(id = id, title = title, locale = locale)
            return (languageDTO != null)
        }
    }

    suspend fun addLanguage() {
        val request = call.receive<AddLanguageRequest>()

        val languageToAddLocale = request.locale
        val languageToAddTitle = request.title

        val checkIfCanAddLanguageResult = checkIfCanAddLanguage(
            languageToAddLocale,
            languageToAddTitle
        )

        if (checkIfCanAddLanguageResult.isValid) {
            val newLanguage = addingLanguageIfNotExist(
                languageToAddLocale,
                languageToAddTitle
            )

            if (newLanguage.id > 0) {
                call.respond(newLanguage)
            } else {
                call.respond(HttpStatusCode.NotFound, "Language is not added")
            }
        } else {
            call.respond(
                checkIfCanAddLanguageResult.statusCode,
                checkIfCanAddLanguageResult.message,
            )
        }
    }

    suspend fun fetchLanguages() {
        call.respond(Language.fetchLanguages())
    }
}
