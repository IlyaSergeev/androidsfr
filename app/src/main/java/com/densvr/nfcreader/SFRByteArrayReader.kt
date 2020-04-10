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

private const val INT_16_SIZE_BYTES = 2
private const val INT_32_SIZE_BYTES = 4

internal fun ByteArray.readInt16Value(position: Int): Int {
    return readIntValue(position, INT_16_SIZE_BYTES)
}

internal fun ByteArray.readInt32Value(position: Int): Int {
    return readIntValue(position, INT_32_SIZE_BYTES)
}

internal fun ByteArray.readSFROperationInfo(position: Int): SFROperationInfo {
    return SFROperationInfo(
        this[position].uint,
        this[position + 1].uint,
        readInt16Value(position + INT_16_SIZE_BYTES).asSFRChipType()
    )
}

internal fun ByteArray.readChipNumber(position: Int): Int {
    return this[position].uint +
            200 * this[position + 1].uint +
            40000 * this[position + 2].uint
}

internal fun ByteArray.readSFRRecordInfo(position: Int): SFRRecordInfo {
    return SFRRecordInfo(
        readInt16Value(position),
        readSFRTime(position + INT_16_SIZE_BYTES)
    )
}

internal fun ByteArray.readSFRTime(position: Int): Long {
    return createDelayMillis(
        hours = readIntFromDecimalFormat(position, INT_16_SIZE_BYTES),
        minutes = readIntFromDecimalFormat(position + INT_16_SIZE_BYTES, INT_16_SIZE_BYTES),
        seconds = readIntFromDecimalFormat(position + 2 * INT_16_SIZE_BYTES, INT_16_SIZE_BYTES)
    )
}

internal fun ByteArray.readIntFromDecimalFormat(position: Int, length: Int): Long {
    var result = 0L
    for (i in 0..length) {
        result = 10 * result + this[position + i]
    }
    return result
}
