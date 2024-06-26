package com.tradume.features.word_pair

import kotlinx.serialization.Serializable

@Serializable
data class AddWordPairRequest(
    val languageFromId: Int,
    val languageToId: Int,
    val wordFrom: String,
    val wordTo: String,
)
