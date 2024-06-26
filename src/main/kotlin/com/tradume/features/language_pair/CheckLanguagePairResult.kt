package com.tradume.features.language_pair

import com.tradume.utils.CheckDataResult

data class CheckLanguagePairResult(
    var languagePairId: Int = 0,
    var languagePairTitle: String = "",
): CheckDataResult()
