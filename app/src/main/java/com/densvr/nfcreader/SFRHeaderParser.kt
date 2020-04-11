package com.densvr.nfcreader

internal fun ByteArray.readSFRHeader(offset: Int): SFRHeader {

    return SFRHeader(
        readInt32Value(offset + SFR_BLOCK_POS_LAST_FORMAT_TIME.sfrBlockOffset).toLong(),
        readSFROperationInfo(offset + SFR_BLOCK_POS_OPERATION.sfrBlockOffset),
        readChipNumber(offset + SFR_BLOCK_POS_CHIP_NUMBER.sfrBlockOffset),
        readSFRPointInfo(offset + SFR_BLOCK_POS_START_STATION.sfrBlockOffset)
    )
}