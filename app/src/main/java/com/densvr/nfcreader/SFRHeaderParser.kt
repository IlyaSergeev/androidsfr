package com.densvr.nfcreader

class SFRHeaderParser(
    private val bytes: ByteArray,
    private val offset: Int
) {
    private fun Int.toBytesPosition(): Int {
        return offset + this * SRF_BLOCK_SIZE_BYTES
    }

    private val lastFormatTime: Long
        get() = bytes.readInt32Value(POS_LAST_FORMAT_TIME.toBytesPosition()).toLong()

    private val operationInfo: SFROperationInfo
        get() = bytes.readSFROperationInfo(POS_OPERATION.toBytesPosition())

    private val chipNumber: Int
        get() = bytes.readChipNumber(POS_CHIP_NUMBER.toBytesPosition())

    private val startPointInfo
        get() = bytes.readSFRPointInfo(POS_START_STATION.toBytesPosition())

    val sfrHeader: SFRHeader
        get() = SFRHeader(lastFormatTime, operationInfo, chipNumber, startPointInfo)
}