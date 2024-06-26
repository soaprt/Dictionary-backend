package com.tradume.database.language_pair

import com.tradume.database.language.LanguageDTO
import kotlinx.serialization.Serializable

@Serializable
data class LanguagePairDTO(
    var id: Int = 0,
    val title: String,
    val languageFromId: Int,
    val languageToId: Int,
)

fun generateLanguagePairTitle(
    languageFrom: LanguageDTO,
    languageTo: LanguageDTO
) = "${languageFrom.title}(${languageFrom.locale}) - ${languageTo.title}(${languageTo.locale})"
