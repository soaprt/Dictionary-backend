package com.tradume.database.language

import kotlinx.serialization.Serializable

@Serializable
data class LanguageDTO(
    var id: Int = 0,
    val title: String,
    val locale: String,
)
