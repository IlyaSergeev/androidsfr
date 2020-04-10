package com.densvr.nfcreader

import kotlin.math.max
import kotlin.math.min

fun ByteArray.readResponseCode(offset: Int): NfcVResponseCode? {

    val operationSize = max(0, size - offset)
    return if (operationSize == 0) {
        NfcVResponseCode.NoStatusInformation
    } else {
        NfcVResponseCode.values().firstOrNull {
            if (it.bytes.isNotEmpty()) {
                val compareBytesCount = min(it.bytes.size, operationSize)
                areEqualByteArrays(
                    this, offset, compareBytesCount,
                    it.bytes, 0, compareBytesCount
                )
            } else {
                false
            }
        }
    }
}