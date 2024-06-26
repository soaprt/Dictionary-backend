package com.tradume.database.language_pair

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object LanguagePair: IntIdTable("language_pair") {
    private val title = LanguagePair.varchar("title", 100)
    private val languageFromId = LanguagePair.integer("language_from_id")
    private val languageToId = LanguagePair.integer("language_to_id")

    fun insert(languagePairDTO: LanguagePairDTO): Int {
        return transaction {
            LanguagePair.insertIgnoreAndGetId {
                it[title] = languagePairDTO.title
                it[languageFromId] = languagePairDTO.languageFromId
                it[languageToId] = languagePairDTO.languageToId
            }?.value ?: -1
        }
    }

    fun fetchLanguagePair(
        languageFromId: Int,
        languageToId: Int
    ): LanguagePairDTO? {
        return try {
            transaction {
                LanguagePair.select {
                    LanguagePair.languageFromId.eq(languageFromId) and
                    (LanguagePair.languageToId.eq(languageToId))
                }.singleOrNull()?.mapToLanguagePairDTO()
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchLanguagePairs(): List<LanguagePairDTO> = transaction {
        LanguagePair.selectAll().toList().map {
            it.mapToLanguagePairDTO()
        }
    }

    private fun ResultRow.mapToLanguagePairDTO(): LanguagePairDTO = LanguagePairDTO(
        id = this[id].value,
        title = this[title],
        languageFromId = this[languageFromId],
        languageToId = this[languageToId],
    )
}
