package com.densvr.nfcreader

private fun Int.toBlockStartPosition(offset: Int): Int {
    return offset + this * SRF_BLOCK_SIZE_BYTES
}

internal fun ByteArray.readSFRHeader(offset: Int): SFRHeader {

    return SFRHeader(
        readInt32Value(POS_LAST_FORMAT_TIME.toBlockStartPosition(offset)).toLong(),
        readSFROperationInfo(POS_OPERATION.toBlockStartPosition(offset)),
        readChipNumber(POS_CHIP_NUMBER.toBlockStartPosition(offset)),
        readSFRPointInfo(POS_START_STATION.toBlockStartPosition(offset))
    )
}