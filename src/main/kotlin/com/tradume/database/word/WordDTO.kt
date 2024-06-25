package com.tradume.database.word

import kotlinx.serialization.Serializable

@Serializable
class WordDTO(
    var id: Int = 0,
    val word: String,
    val languageId: Int,
)
