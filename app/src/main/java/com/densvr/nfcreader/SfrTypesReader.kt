package com.densvr.nfcreader

import com.densvr.util.areEquals
import kotlin.math.max
import kotlin.math.min

/**
 * Created by i-sergeev on 11.04.2020.
 */
internal fun ByteArray.readResponseCode(offset: Int): NfcResponseCode {

    val operationSize = max(0, size - offset)
    return if (operationSize == 0) {
        NfcResponseCode.NoStatusInformation
    } else {
        NfcResponseCode.values().firstOrNull {
            if (it.bytes.isNotEmpty()) {
                val compareBytesCount = min(it.bytes.size, operationSize)
                areEquals(
                    this, offset, compareBytesCount,
                    it.bytes, 0, compareBytesCount
                )
            } else {
                false
            }
        } ?: NfcResponseCode.UnknownResponse
    }
}

internal fun ByteArray.readSFRHeader(offset: Int): SfrHeader {

    return SfrHeader(
        readInt32Value(offset + SFR_BLOCK_POS_LAST_FORMAT_TIME.sfrBlockOffset).toLong(),
        readSFROperationInfo(offset + SFR_BLOCK_POS_OPERATION.sfrBlockOffset),
        readChipNumber(offset + SFR_BLOCK_POS_CHIP_NUMBER.sfrBlockOffset),
        readSFRPointInfo(offset + SFR_BLOCK_POS_BASE_POINT.sfrBlockOffset)
    )
}

internal fun ByteArray.readSFROperationInfo(position: Int): SfrOperationInfo {
    return SfrOperationInfo(
        this[position + 3 * ONE_BYTE].uint,
        this[position + 1].uint,
        this[position].uint.asSFRChipType()
    )
}

private fun Int.asSFRChipType(): SfrChipType? {
    return when (this) {
        0 -> SfrChipType.COMPETITOR
        1 -> SfrChipType.SERVICE
        else -> null
    }
}

internal fun ByteArray.readChipNumber(position: Int): Int {
    return this[position].uint +
            200 * this[position + 1].uint +
            40000 * this[position + 2].uint
}

fun ByteArray.readSFRPointInfo(position: Int): SFRPointInfo {
    return SFRPointInfo(
        this[position + 3 * ONE_BYTE].uint,
        readSFRTime(position)
    )
}

internal fun ByteArray.readSFRTime(position: Int): Long {
    return createDelayMillis(
        hours = readIntFromDecimalFormat(position, ONE_BYTE),
        minutes = readIntFromDecimalFormat(position + ONE_BYTE, ONE_BYTE),
        seconds = readIntFromDecimalFormat(position + 2 * ONE_BYTE, ONE_BYTE)
    )
}