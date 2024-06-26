package com.tradume.features.language_pair

import kotlinx.serialization.Serializable

@Serializable
data class AddLanguagePairRequest(
    val languageFromId: Int,
    val languageToId: Int,
)
