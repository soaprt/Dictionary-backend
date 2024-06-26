package com.tradume.features.word

import kotlinx.serialization.Serializable

@Serializable
data class AddWordRequest(
    val word: String,
    val languageId: Int,
)
