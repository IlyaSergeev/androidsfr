package com.densvr.nfcreader

class NfcVResponseParser(
    val bytes: ByteArray,
    private val offset: Int,
    private val length: Int
) {
    val responseCode: NfcVResponseCode
        get() = bytes.readResponseCode(offset)
}