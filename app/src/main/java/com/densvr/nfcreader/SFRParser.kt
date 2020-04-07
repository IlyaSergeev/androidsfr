package com.densvr.nfcreader

class SFRParser(private val bytes: ByteArray, private val offset: Int, private val length: Int) {

    companion object {
        private const val INT32_BYES_COUNT = 4

        private const val POS_LAST_FORMAT_TIME = 1
        private const val POS_CHIP_NUMBER = 3
        private const val POS_OPERATION = 4
        private const val POS_START_STATION = 5
        private const val POS_FIRST_RECORD = 6
    }

    private fun Int.toBytesPosition(): Int {
        return offset + this * INT32_BYES_COUNT
    }

    fun readIntAt(position: Int): Int {
        return bytes.readInt32Value(position.toBytesPosition())
    }

    val lastFormatTime: Long
        get() = readIntAt(POS_LAST_FORMAT_TIME).toLong()

    val operationInfo: SFROperationInfo
        get() = bytes.readSFROperationInfo(POS_OPERATION.toBytesPosition())

    val chipNumber: Int
        get() = bytes.readChipNumber(POS_CHIP_NUMBER.toBytesPosition())

    val startPointInfo
        get() = bytes.readSFRRecordInfo(POS_START_STATION.toBytesPosition())

    val pointInfoCount
        get() = (length / INT32_BYES_COUNT) - POS_START_STATION

    fun pointInfoAt(position: Int): SFRRecordInfo {
        return bytes.readSFRRecordInfo((POS_FIRST_RECORD + position).toBytesPosition())
    }
}