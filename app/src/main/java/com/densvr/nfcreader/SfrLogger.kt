package com.densvr.nfcreader

import com.densvr.util.asHex

internal fun ByteArray.asNumeratedString(
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