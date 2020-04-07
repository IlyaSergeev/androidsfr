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

private fun ByteArray.readIntValue(position: Int, intLength: Int): Int {
    var result = 0
    for (i in position until (position + intLength)) {
        result = (result shl 8) or (0xFF and this[i].toInt())
    }
    return result
}

private const val INT_16_SIZE_BYTES = 2
internal fun ByteArray.readInt16Value(position: Int): Int {
    return readIntValue(position, INT_16_SIZE_BYTES)
}

private const val INT_32_SIZE_BYTES = 2
internal fun ByteArray.readInt32Value(position: Int): Int {
    return readIntValue(position, INT_32_SIZE_BYTES)
}

internal fun ByteArray.readSFROperationInfo(position: Int): SFROperationInfo {
    return SFROperationInfo(
        readInt16Value(position),
        readInt16Value(position + 2).asSFRChipType()
    )
}

internal fun ByteArray.readChipNumber(position: Int): Int {
    return readInt16Value(position) +
            200 * readInt16Value(position + INT_16_SIZE_BYTES) +
            40000 * readInt16Value(position + 2 * INT_16_SIZE_BYTES)
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