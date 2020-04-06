package com.densvr.nfcreader

import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

fun String.tryParseDelayMillisOrZero(): Long {

    val timeLexems = split(":").reversed()
    val secondWithMillisLexems = timeLexems.getOrNull(0)?.split(".")

    return secondWithMillisLexems?.getOrNull(1).toNormalMillisString().parseTimeOrZero(TimeUnit.MILLISECONDS) +
            secondWithMillisLexems?.getOrNull(0).parseTimeOrZero(TimeUnit.SECONDS) +
            timeLexems.getOrNull(1).parseTimeOrZero(TimeUnit.MINUTES) +
            timeLexems.getOrNull(2).parseTimeOrZero(TimeUnit.HOURS)
}

private fun String?.toNormalMillisString(): String? {
    return this?.take(3)?.padEnd(3, '0')
}

private fun String?.parseTimeOrZero(timeUnit: TimeUnit): Long {
    return timeUnit.toMillis(this?.toLongOrNull() ?: 0L)
}

fun Long.toDelayTime(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)

    val stringWithoutHours = this - TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(stringWithoutHours)

    val stringWithoutHoursAndSecond = stringWithoutHours - TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this)

    val millis = stringWithoutHoursAndSecond - TimeUnit.SECONDS.toMillis(seconds)

    val result = StringBuilder()
    var addWithoutCheck = false
    if (hours > 0) {
        addWithoutCheck = true
        result.append("%02d:".format(hours))
    }
    if (addWithoutCheck || minutes > 0) {
        addWithoutCheck = true
        result.append("%02d:".format(minutes))
    }
    if (addWithoutCheck || seconds > 0) {
        addWithoutCheck = true
        result.append("%02d".format(seconds))
    }
    if (millis > 0) {
        result.append("%03d:".format(millis))
    }
    return result.toString()
}