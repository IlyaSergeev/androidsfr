package com.densvr.nfcreader

import java.util.concurrent.TimeUnit

fun String.tryParseDelayMillisOrZero(): Long {

    val timeLexems = split(":").reversed()
    val secondWithMillisLexems = timeLexems.getOrNull(0)?.split(".")

    return secondWithMillisLexems?.getOrNull(1)?.substring(0, 3).parseTimeOrZero(TimeUnit.MILLISECONDS) +
            secondWithMillisLexems?.getOrNull(0).parseTimeOrZero(TimeUnit.SECONDS) +
            timeLexems.getOrNull(1).parseTimeOrZero(TimeUnit.MINUTES) +
            timeLexems.getOrNull(2).parseTimeOrZero(TimeUnit.HOURS)
}

private fun String?.parseTimeOrZero(timeUnit: TimeUnit): Long {
    return timeUnit.toMillis(this?.toLongOrNull() ?: 0L)
}

fun Long.toDelayTime(): String {
    return ""
}