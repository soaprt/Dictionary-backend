package com.tradume.database.word_pair

import kotlinx.serialization.Serializable

@Serializable
data class WordPairDTO(
    var id: Int = 0,
    val wordFromId: Int,
    val wordToId: Int,
    val languagePairId: Int,
)
