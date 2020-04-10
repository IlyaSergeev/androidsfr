package com.densvr.nfcreader

fun ByteArray.readResponseCode(offset: Int, length: Int) : NfcVResponseCode? {

    return if (offset == length) {
        NfcVResponseCode.NoStatusInformation
    }
    else {
        val byte1 = this[offset]
        val byte2 = this[offset + 1]

        NfcVResponseCode.values().firstOrNull {
            if (it.bytes.size > 1) {
                byte1 == it.bytes[0] && byte2 == it.bytes[1]
            }
            else {
                false
            }
        }
    }
}