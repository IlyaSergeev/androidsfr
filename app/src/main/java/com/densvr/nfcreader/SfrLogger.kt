package com.densvr.nfcreader

import com.densvr.util.asHex
import timber.log.Timber

fun ByteArray.asNumeratedString(firstIndex: Int = 0): String {
    return asNumeratedString(
        offsetPrefix = "h: ",
        chunkLength = 8,
        firstIndex = firstIndex,
        separator = "\n"
    )
}

private fun ByteArray.asNumeratedString(
    offsetPrefix: String,
    chunkLength: Int,
    firstIndex: Int,
    separator: String
): String {
    val hexString = asHex
    val offset = hexString.length % chunkLength

    return if (offset > 0) {
        offsetPrefix + hexString.take(offset)
    } else {
        ""
    } + separator +
            hexString.substring(offset)
                .chunked(chunkLength)
                .mapIndexed { index, chunkHexString ->
                    "%03d: %s".format(
                        firstIndex + index,
                        chunkHexString
                    )
                }
                .joinToString(separator)
}

private const val NFC_READER_TAG = "NFC Reader"

internal fun ByteArray.logAsNfcMessage(startTagNumber: Int, operation: String) {

    Timber.tag(NFC_READER_TAG).apply {
        d(operation)
        d(asNumeratedString(firstIndex = startTagNumber))
    }
}