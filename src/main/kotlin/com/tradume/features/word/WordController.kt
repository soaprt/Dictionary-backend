package com.tradume.features.word

import com.tradume.database.word.Word
import com.tradume.database.word.WordDTO
import com.tradume.features.FetchMultipleDataResult
import com.tradume.features.language.LanguageController
import com.tradume.features.toFetchMultipleDataResult
import com.tradume.utils.CheckDataResult
import com.tradume.utils.hasValidIdValue
import com.tradume.utils.hasValue
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class WordController(private val call: ApplicationCall) {
    companion object {
        fun addingWordIfNotExist(
            word: String,
            languageId: Int,
        ): WordDTO {
            val wordDTO = WordDTO(
                word = word,
                languageId = languageId
            )

            wordDTO.id = Word.insert(wordDTO)

            return wordDTO
        }

        fun checkWords(
            languageFromId: Int,
            languageToId: Int,
            wordFrom: String,
            wordTo: String
        ): CheckWordsResult {
            var result = CheckWordsResult()

            val checkWordFromResult = checkIfWordValid(wordFrom)
            if (!checkWordFromResult.isValid) {
                result.apply {
                    isValid = false
                    statusCode = checkWordFromResult.statusCode
                    message = checkWordFromResult.message
                }
            }

            if (result.isValid) {
                val checkWordToResult = checkIfWordValid(wordTo)
                if (!checkWordToResult.isValid) {
                    result.apply {
                        isValid = false
                        statusCode = checkWordToResult.statusCode
                        message = checkWordToResult.message
                    }
                }
            }

            if (result.isValid && (wordFrom == wordTo)) {
                result.apply {
                    isValid = false
                    statusCode = HttpStatusCode.BadRequest
                    message = "Words should differ"
                }
            }

            if (result.isValid) {
                val wordFromId = Word.fetchWord(word = wordFrom, languageId = languageFromId)?.id ?: 0
                val wordToId = Word.fetchWord(word = wordTo, languageId = languageToId)?.id ?: 0

                result = result.copy(
                    wordFromId = wordFromId,
                    wordToId = wordToId
                )
            }

            return result
        }

        fun checkIfWordValid(word: String): CheckDataResult {
            val result = CheckDataResult()

            if (!word.hasValue()) {
                result.apply {
                    isValid = false
                    statusCode = HttpStatusCode.BadRequest
                    message = "Word is not specified"
                }
            }

            return result
        }

        fun fetchWord(id: Int): WordDTO? = Word.fetchWord(id = id)
    }

    suspend fun addWord() {
        val request = call.receive<AddWordRequest>()

        val wordToAdd = request.word
        val wordToAddLanguageId = request.languageId

        val checkIfCanAddWordResult = checkIfCanAddWord(
            wordToAddLanguageId,
            wordToAdd,
        )

        if (checkIfCanAddWordResult.isValid) {
            val newWord = addingWordIfNotExist(
                wordToAdd,
                wordToAddLanguageId,
            )

            if (newWord.id > 0) {
                call.respond(newWord)
            } else {
                call.respond(HttpStatusCode.NotFound, "Word is not added")
            }
        } else {
            call.respond(
                checkIfCanAddWordResult.statusCode,
                checkIfCanAddWordResult.message
            )
        }
    }

    private fun checkIfCanAddWord(
        languageId: Int,
        word: String,
    ): CheckDataResult {
        val result = checkIfWordValid(word)

        if (result.isValid && !languageId.hasValidIdValue()) {
            result.apply {
                isValid = false
                statusCode = HttpStatusCode.BadRequest
                message = "Id is not specified"
            }
        }

        if (result.isValid && !LanguageController.hasLanguage(id = languageId)) {
            result.apply {
                isValid = false
                statusCode = HttpStatusCode.NotFound
                message = "Language does not exist"
            }
        }

        return result
    }

    suspend fun fetchWords() {
        call.respond(
            toFetchMultipleDataResult(Word.fetchWords())
        )
    }
}
