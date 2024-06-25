package com.tradume.utils

fun String.hasValue() = (isNotBlank() && isNotEmpty())

fun Int.hasValidIdValue() = this > 0
