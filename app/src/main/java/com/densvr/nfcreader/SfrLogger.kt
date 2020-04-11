package com.densvr.nfcreader

import com.densvr.util.asHex
import timber.log.Timber

private fun ByteArray.asNumeratedString(
    offsetPrefix: String,
    chunkLength: Int,
    firstIndex: Int,
    separator: String
): String {
    val hexString = asHex
    val offset = hexString.length % chunkLength

    return offsetPrefix + hexString.take(offset) + separator +
            hexString.substring(offset)
                .chunked(chunkLength)
                .mapIndexed { index, chunkHexString -> "${firstIndex + index}: $chunkHexString" }
                .joinToString(separator)
}

private const val NFC_READER_TAG = "NFC Reader"

internal fun ByteArray.logAsNfcMessage(startTagNumber: Int, operation: String) {

    Timber.tag(NFC_READER_TAG).apply {
        d(operation)
        d(
            asNumeratedString(
                offsetPrefix = "h: ",
                chunkLength = 8,
                firstIndex = startTagNumber,
                separator = "\n"
            )
        )
    }
}