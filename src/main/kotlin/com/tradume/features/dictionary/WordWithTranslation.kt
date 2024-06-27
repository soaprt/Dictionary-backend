package com.tradume.features.dictionary

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class WordWithTranslation(
    val id: String,
    val word: String
)

fun toWordWithTranslation(word: String, translationVariants: List<String>) = WordWithTranslation(
    id = UUID.randomUUID().toString(),
    word = "$word - ${translationVariants.joinToString(", ")}"
)