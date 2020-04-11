package com.densvr.nfcreader

/**
 * Created by i-sergeev on 07.04.2020.
 */
private fun Int.asSFRChipType(): SFRChipType? {
    return when (this) {
        0 -> SFRChipType.COMPETITOR
        1 -> SFRChipType.SERVICE
        else -> null
    }
}

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

internal fun ByteArray.readSFROperationInfo(position: Int): SFROperationInfo {
    return SFROperationInfo(
        this[position + 3 * ONE_BYTE].uint,
        this[position + 1].uint,
        this[position].uint.asSFRChipType()
    )
}

internal fun ByteArray.readChipNumber(position: Int): Int {
    return this[position].uint +
            200 * this[position + 1].uint +
            40000 * this[position + 2].uint
}

fun ByteArray.readSFRPointInfo(position: Int): SFRPointInfo {
    return SFRPointInfo(
        this[position].uint,
        readSFRTime(position + ONE_BYTE)
    )
}

internal fun ByteArray.readSFRTime(position: Int): Long {
    return createDelayMillis(
        hours = readIntFromDecimalFormat(position, ONE_BYTE),
        minutes = readIntFromDecimalFormat(position + ONE_BYTE, ONE_BYTE),
        seconds = readIntFromDecimalFormat(position + 2 * ONE_BYTE, ONE_BYTE)
    )
}

internal fun ByteArray.readIntFromDecimalFormat(position: Int, length: Int): Long {
    var result = 0L
    for (i in 0 until length) {
        result = 10 * result + this[position + i]
    }
    return result
}

internal fun ByteArray.isEmptySFRBlock(offset: Int): Boolean {
    for (i in 0 until SRF_BLOCK_SIZE_BYTES) {
        if (this[offset + i] != ZERO_BYTE) {
            return false
        }
    }
    return true
}

internal fun areEqualByteArrays(
    byteArray1: ByteArray, offset1: Int, length1: Int,
    byteArray2: ByteArray, offset2: Int, length2: Int
): Boolean {

    return if (length1 == length2) {
        for (i in 0 until length1) {
            if (byteArray1[offset1 + i] != byteArray2[offset2 + i]) {
                return false
            }
        }
        true
    }
    else {
        false
    }
}