package com.tradume.features.language

import kotlinx.serialization.Serializable

@Serializable
data class AddLanguageRequest(
    val title: String,
    val locale: String,
)
