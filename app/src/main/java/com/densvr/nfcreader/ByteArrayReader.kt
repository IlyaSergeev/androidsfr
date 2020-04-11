package com.densvr.nfcreader

/**
 * Created by i-sergeev on 07.04.2020.
 */
internal val Byte.uint
    inline get() = (0xFF and toInt())

private fun ByteArray.readIntValue(position: Int, intLength: Int): Int {
    var result = 0
    for (i in position until (position + intLength)) {
        result = (result shl 8) or this[i].uint
    }
    return result
}

internal fun ByteArray.readInt16Value(position: Int): Int {
    return readIntValue(position, INT16_SIZE_BYTES)
}

internal fun ByteArray.readInt32Value(position: Int): Int {
    return readIntValue(position, INT32_SIZE_BYTES)
}

internal fun ByteArray.readIntFromDecimalFormat(position: Int, length: Int): Long {
    var result = 0L
    for (i in 0 until length) {
        result = 10 * result + this[position + i]
    }
    return result
}