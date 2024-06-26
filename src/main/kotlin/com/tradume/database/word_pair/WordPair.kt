package com.tradume.database.word_pair

import com.tradume.database.word_pair.WordPair.mapToWordPairDTO
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object WordPair: IntIdTable("word_pair") {
    private val wordFromId = WordPair.integer("word_from_id")
    private val wordToId = WordPair.integer("word_to_id")
    private val languagePairId = WordPair.integer("language_pair_id")

    fun insert(wordPairDTO: WordPairDTO): Int {
        return transaction {
            WordPair.insertIgnoreAndGetId {
                it[wordFromId] = wordPairDTO.wordFromId
                it[wordToId] = wordPairDTO.wordToId
                it[languagePairId] = wordPairDTO.languagePairId
            }?.value ?: -1
        }
    }

    /*fun fetchWordPair(
        wordFromId: Int,
        wordToId: Int,
        languagePairId: Int
    ): WordPairDTO? {
        return try {
            transaction {
                WordPair.select {
                    WordPair.wordFromId.eq(wordFromId) and
                    WordPair.wordToId.eq(wordToId) and
                    WordPair.languagePairId.eq(languagePairId)
                }.singleOrNull()?.mapToWordPairDTO()
            }
        } catch (e: Exception) {
            null
        }
    }*/

    fun fetchWordPairs(languagePairId: Int = 0): List<WordPairDTO> = transaction {
        generateWhereExpression(languagePairId).let { whereExp ->
            WordPair.select { whereExp }.toList().map {
                it.mapToWordPairDTO()
            }
        }
    }

    private fun generateWhereExpression(
        languagePairId: Int
    ) = when {
        (languagePairId > 0) -> Op.build { WordPair.languagePairId.eq(languagePairId) }
        else -> Op.build { WordPair.id.greater(0) }  // all
    }

    private fun ResultRow.mapToWordPairDTO(): WordPairDTO = WordPairDTO(
        id = this[id].value,
        wordFromId = this[wordFromId],
        wordToId = this[wordToId],
        languagePairId = this[languagePairId],
    )
}
