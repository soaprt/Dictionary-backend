package com.tradume.database.word

import com.tradume.database.language.Language
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Word: IntIdTable() {
    private val word = Word.varchar("word", 50)
    private val languageId = Word.integer("language_id")

    fun insert(wordDTO: WordDTO): Int {
        return transaction {
            Word.insertIgnoreAndGetId {
                it[word] = wordDTO.word
                it[languageId] = wordDTO.languageId
            }?.value ?: -1
        }
    }

    fun fetchWord(
        id: Int = 0,
        word: String = "",
        languageId: Int = 0
    ): WordDTO? {
        return try {
            transaction {
                generateWhereExpression(id, word, languageId)?.let { whereExp ->
                    Word.select { whereExp }.singleOrNull()?.mapToWordDTO()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun generateWhereExpression(
        id: Int,
        word: String,
        languageId: Int
    ) = when {
        (id > 0) -> Op.build { Word.id.eq(id) }
        (word.isNotEmpty() && languageId > 0) -> Op.build {
            Word.word.eq(word) and Word.languageId.eq(languageId)
        }
        else -> null
    }

    fun fetchWords(): List<WordDTO> = transaction {
        Word.selectAll().toList().map {
            it.mapToWordDTO()
        }
    }

    private fun ResultRow.mapToWordDTO(): WordDTO = WordDTO(
        id = this[id].value,
        word = this[word],
        languageId = this[languageId]
    )
}
