package com.tradume.features.word_pair

import com.tradume.database.word_pair.WordPair
import com.tradume.database.word_pair.WordPairDTO
import com.tradume.features.language_pair.CheckLanguagePairResult
import com.tradume.features.language_pair.LanguagePairController
import com.tradume.features.word.CheckWordsResult
import com.tradume.features.word.WordController
import com.tradume.utils.CheckDataResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class WordPairController(private val call: ApplicationCall) {
    companion object {
        fun fetchWordPairs(languagePairId: Int): List<WordPairDTO> = WordPair.fetchWordPairs(languagePairId)
    }

    suspend fun addWordPair() {
        val request = call.receive<AddWordPairRequest>()

        val languageFromId: Int = request.languageFromId
        val languageToId: Int = request.languageToId
        val wordFrom: String = request.wordFrom
        val wordTo: String = request.wordTo

        val checkIfCanAddWordPairResults = checkIfCanAddWordPair(
            languageFromId,
            languageToId,
            wordFrom,
            wordTo
        )
        val checkResult = CheckDataResult()
        var languagePairId = 0
        var wordFromId = 0
        var wordToId = 0

        check@for (item in checkIfCanAddWordPairResults) {
            if (item.isValid) {
                when (item) {
                    is CheckLanguagePairResult -> {
                        languagePairId = item.languagePairId
                    }

                    is CheckWordsResult -> {
                        wordFromId = item.wordFromId
                        wordToId = item.wordToId
                    }
                }
            } else {
                checkResult.apply {
                    isValid = false
                    statusCode = item.statusCode
                    message = item.message
                }
                break@check
            }
        }

        if (checkResult.isValid) {
            val newWordPair = addingWordPairIfNotExist(
                languagePairId,
                languageFromId,
                languageToId,
                wordFrom,
                wordFromId,
                wordTo,
                wordToId,
            )

            if (newWordPair != null && newWordPair.id > 0) {
                call.respond(newWordPair)
            } else {
                call.respond(HttpStatusCode.NotFound, "Word pair is not added")
            }
        } else {
            call.respond(
                checkResult.statusCode,
                checkResult.message
            )
        }
    }

    private fun addingWordPairIfNotExist(
        languagePairId: Int,
        languageFromId: Int,
        languageToId: Int,
        wordFrom: String,
        wordFromId: Int,
        wordTo: String,
        wordToId: Int,
    ): WordPairDTO? {
        val wordFromResultId = if (wordFromId == 0) {
            WordController.addingWordIfNotExist(
                wordFrom,
                languageFromId,
            ).id
        } else {
            wordFromId
        }

        val wordToResultId = if (wordToId == 0) {
            WordController.addingWordIfNotExist(
                wordTo,
                languageToId,
            ).id
        } else {
            wordToId
        }

        return if (wordFromResultId > 0 && wordToResultId > 0) {
            val wordPairDTO = WordPairDTO(
                wordFromId = wordFromResultId,
                wordToId = wordToResultId,
                languagePairId = languagePairId
            )
            wordPairDTO.id = WordPair.insert(wordPairDTO)

            wordPairDTO
        } else {
            null
        }
    }

    private fun checkIfCanAddWordPair(
        languageFromId: Int,
        languageToId: Int,
        wordFrom: String,
        wordTo: String
    ): List<CheckDataResult> {
        val result: MutableList<CheckDataResult> = mutableListOf()

        val checkLanguagePairResult = LanguagePairController.checkLanguagePair(languageFromId, languageToId)
        result.add(checkLanguagePairResult)

        if (checkLanguagePairResult.isValid) {
            val checkWordsResult = WordController.checkWords(languageFromId, languageToId, wordFrom, wordTo)
            result.add(checkWordsResult)
        }

        return result
    }

    suspend fun fetchAllWordPairs() {
        call.respond(WordPair.fetchWordPairs())
    }
}
