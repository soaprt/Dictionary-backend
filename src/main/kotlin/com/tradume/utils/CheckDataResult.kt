package com.tradume.utils

import io.ktor.http.*

open class CheckDataResult(
    var isValid: Boolean = true,
    var statusCode: HttpStatusCode = HttpStatusCode.OK,
    var message: String = ""
)
