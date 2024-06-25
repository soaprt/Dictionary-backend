package com.tradume.database.language

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Language: IntIdTable() {
    private val title = Language.varchar("title", 50)
    private val locale = Language.varchar("locale", 5).uniqueIndex()

    fun insert(languageDTO: LanguageDTO): Int {
        return transaction {
            Language.insertIgnoreAndGetId {
                it[title] = languageDTO.title
                it[locale] = languageDTO.locale
            }?.value ?: -1
        }
    }

    fun fetchLanguage(
        id: Int,
        title: String,
        locale: String,
    ): LanguageDTO? {
        return try {
            transaction {
                generateWhereExpression(id, title, locale)?.let { whereExp ->
                    Language.select { whereExp }.singleOrNull()?.mapToLanguageDTO()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchLanguages(): List<LanguageDTO> = transaction {
        Language.selectAll().toList().map {
            it.mapToLanguageDTO()
        }
    }

    private fun generateWhereExpression(
        id: Int,
        title: String,
        locale: String,
    ) = when {
        (id > 0) -> Op.build { Language.id.eq(id) }
        (title.isNotEmpty() && locale.isNotEmpty()) -> Op.build {
            Language.title.eq(title) and Language.locale.eq(locale)
        }
        else -> null
    }

    private fun ResultRow.mapToLanguageDTO(): LanguageDTO = LanguageDTO(
        id = this[id].value,
        title = this[title],
        locale = this[locale]
    )
}