package com.densvr.nfcreader

import kotlin.math.max

class NfcVResponseParser(
    val bytes: ByteArray,
    private val offset: Int,
    private val length: Int
) {
    companion object {
        const val RESPONSE_CODE_LENGTH = 2 * BYTE_SIZE
    }

    val contentOffset: Int = RESPONSE_CODE_LENGTH + offset
    val contentLength: Int = max(0, length - offset)

    val responseCode: NfcVResponseCode?
        get() = bytes.readResponseCode(offset, length)
}