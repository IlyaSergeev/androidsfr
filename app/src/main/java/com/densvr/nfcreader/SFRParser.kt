package com.densvr.nfcreader

class SFRParser(private val bytes: ByteArray, private val offset: Int, private val length: Int) {

    companion object {
        private const val INT32_BYES_COUNT = 4

        private const val POS_LAST_FORMAT_TIME = 1
        private const val POS_CHIP_NUMBER = 3
        private const val POS_OPERATION = 4
        private const val POS_START_STATION = 5
        private const val POS_FIRST_RECORD = 6

        private const val OPERATION_TYPE_OFFSET = 2
    }

    private fun Int.toBytesPosition(): Int {
        return offset + this * INT32_BYES_COUNT
    }

    fun getIntAt(position: Int): Int {
        return bytes.getInt32Value(position.toBytesPosition())
    }

    val lastFormatTime
        get() = getIntAt(POS_LAST_FORMAT_TIME)

    val operationInfo: SFROperationInfo
        get() = POS_OPERATION.toBytesPosition().let { position ->
            SFROperationInfo(
                bytes.getInt16Value(position),
                bytes.getInt16Value(position + OPERATION_TYPE_OFFSET).asSFRChipType()
            )
        }

    val chipNumber: Int
    get() = POS_CHIP_NUMBER.toBytesPosition().let { position ->
        
    }
}

private fun Int.asSFRChipType(): SFRChipType? {
    return when (this) {
        0 -> SFRChipType.COMPETITOR
        1 -> SFRChipType.SERVICE
        else -> null
    }
}

private fun ByteArray.getInt16Value(position: Int): Int {
    return (this[0].toInt() shl 8) + this[position + 1]
}

private fun ByteArray.getInt32Value(position: Int): Int {
    return (this[0].toInt() shl 24) + (this[position + 1].toInt() shl 16) + (this[position + 2].toInt() shl 8) + this[position + 3]
}