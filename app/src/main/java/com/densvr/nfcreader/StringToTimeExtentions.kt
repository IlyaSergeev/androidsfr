package com.densvr.nfcreader

import java.text.SimpleDateFormat
import java.util.*

private const val timeFormatHHmmss = "HH:mm:ss"
private const val timeFormatMmss = "mm:ss"
private const val timeFormatSs = "ss"

fun String.tryToTimeMillis(): Long? {
    return tryParseTimeMillisWithFormat(timeFormatSs, timeFormatMmss, timeFormatHHmmss)?.time
}

private fun String.tryParseTimeMillisWithFormat(vararg timeFormats: String): Date? {
    timeFormats.forEach { simpleDateFormat ->
        try {
            return getTimeFormat(simpleDateFormat).parse(this)
        } catch (error: Throwable) {
        }
    }
    return null
}

private val utcTimeZone = TimeZone.getTimeZone("UTC")

private fun getTimeFormat(template: String): SimpleDateFormat {
    return SimpleDateFormat(template, Locale.getDefault()).apply {
        timeZone = utcTimeZone
    }
}