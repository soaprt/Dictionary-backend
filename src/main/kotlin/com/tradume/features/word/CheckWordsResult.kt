package com.tradume.features.word

import com.tradume.utils.CheckDataResult

data class CheckWordsResult(
    var wordFromId: Int = 0,
    var wordToId: Int = 0,
): CheckDataResult()
