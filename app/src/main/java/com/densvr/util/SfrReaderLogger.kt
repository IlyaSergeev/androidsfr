package com.densvr.util

import android.graphics.Color
import android.text.SpannableStringBuilder
import com.densvr.nfcreader.asNumeratedString

/**
 * Created by i-sergeev on 15.04.2020.
 */
class SfrReaderLogger {

    private val stringBuilder = SpannableStringBuilder()

    val log: CharSequence
        get() = stringBuilder

    fun clear() {
        stringBuilder.clear()
    }

    fun appendBytes(bytes: ByteArray, offset: Int, length: Int, positionIndex: Int) {
        appendMessage(bytes.asNumeratedString(offset, length, positionIndex))
    }

    fun appendError(errorMessage: CharSequence) {
        appendLn()
        appendMessage(errorMessage.withColor(Color.RED))
    }

    fun appendError(error: Throwable) {
        error.message?.let { errorMessage ->
            appendError(errorMessage)
        }
    }

    fun appendMessage(message: CharSequence) {
        stringBuilder.append(message)
    }

    fun appendLn() {
        stringBuilder.appendln()
    }

}