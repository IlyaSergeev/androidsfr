package com.densvr.nfcreader

class SFRParser(private val bytes: ByteArray, private val offset: Int, private val length: Int) {

    companion object {
        private const val INT32_BYES_COUNT = 4
        private const val POS_FIRST_RECORD = 6
    }

    private fun Int.toBytesPosition(): Int {
        return offset + this * INT32_BYES_COUNT
    }

    val pointInfoCount
        get() = length / INT32_BYES_COUNT

    fun pointInfoAt(position: Int): SFRRecordInfo {
        return bytes.readSFRRecordInfo((POS_FIRST_RECORD + position).toBytesPosition())
    }
}