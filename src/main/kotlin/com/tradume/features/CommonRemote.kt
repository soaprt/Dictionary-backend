package com.tradume.features

import kotlinx.serialization.Serializable

@Serializable
data class FetchMultipleDataResult<E>(
    val items: List<E>
)

fun <E: Any> toFetchMultipleDataResult(data: List<E>) = FetchMultipleDataResult(
    items = data
)