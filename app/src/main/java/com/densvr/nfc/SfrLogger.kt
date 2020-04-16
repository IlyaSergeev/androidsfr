package com.densvr.nfc

import com.densvr.util.asHex
import timber.log.Timber

fun ByteArray.asNumeratedString(firstIndex: Int = 0): String {
    return asNumeratedString(
        offset = 0,
        length = size,
        offsetPrefix = "h: ",
        chunkLength = 8,
        firstIndex = firstIndex,
        separator = "\n"
    )
}

fun ByteArray.asNumeratedString(
    offset: Int,
    length: Int,
    firstIndex: Int = 0
): String {
    return asNumeratedString(
        offset = offset,
        length = length,
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
    return asNumeratedString(
        offset = 0,
        length = size,
        offsetPrefix = offsetPrefix,
        chunkLength = chunkLength,
        firstIndex = firstIndex,
        separator = separator
    )
}

private fun ByteArray.asNumeratedString(
    offset: Int,
    length: Int,
    offsetPrefix: String,
    chunkLength: Int,
    firstIndex: Int,
    separator: String
): String {
    val hexString = asHex(offset, length)
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

internal fun ByteArray.logAsNfcReadMessage(startTagNumber: Int, operation: String) {

    Timber.tag(NFC_READER_TAG).d(operation)
    Timber.tag(NFC_READER_TAG).d(asNumeratedString(firstIndex = startTagNumber))
}

private const val NFC_WRITER_TAG = "NFC Writer"
internal fun ByteArray.logAsNfcWriteMessage(tagIndex: Byte, bytes: ByteArray, operation: String) {

    Timber.tag(NFC_WRITER_TAG).d("$operation $tagIndex: ${bytes.asHex} ->> $asHex")
}