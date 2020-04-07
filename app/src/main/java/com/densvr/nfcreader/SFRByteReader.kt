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

internal fun ByteArray.readInt16Value(position: Int): Int {
    return (this[0].toInt() shl 8) + this[position + 1]
}

internal fun ByteArray.readInt32Value(position: Int): Int {
    return (this[0].toInt() shl 24) + (this[position + 1].toInt() shl 16) + (this[position + 2].toInt() shl 8) + this[position + 3]
}

internal fun ByteArray.readSFROperationInfo(position: Int): SFROperationInfo {
    return SFROperationInfo(
        readInt16Value(position),
        readInt16Value(position + 2).asSFRChipType()
    )
}

internal fun ByteArray.readChipNumber(position: Int): Int {
    return readInt16Value(position) +
            200 * readInt16Value(position + 2) +
            40000 * readInt16Value(position + 4)
}

internal fun ByteArray.readSFRRecordInfo(position: Int): SFRRecordInfo {
    return SFRRecordInfo(
        readInt16Value(position),
        readSFRTime(position + 2)
    )
}

internal fun ByteArray.readSFRTime(position: Int): Long {
    return createDelayMillis(
        hours = readIntFromDecimalFormat(position, 2),
        minutes = readIntFromDecimalFormat(position + 2, 2),
        seconds = readIntFromDecimalFormat(position + 4, 2)
    )
}

internal fun ByteArray.readIntFromDecimalFormat(position: Int, length: Int): Long {
    var result = 0L
    for (i in 0..length) {
        result = 10 * result + this[position + i]
    }
    return result
}