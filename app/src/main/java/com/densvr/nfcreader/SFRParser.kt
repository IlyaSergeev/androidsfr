package com.densvr.nfcreader

class SFRParser(private val bytes: ByteArray, private val offset: Int, private val length: Int) {

    companion object {
        const val POS_FIRST_RECORD = 0x06
    }

    private fun Int.toBytesPosition(): Int {
        return offset + this * INT32_SIZE_BYTES
    }

    val pointInfoCount
        get() = length / INT32_SIZE_BYTES

    fun pointInfoAt(position: Int): SFRPointInfo {
        return bytes.readSFRPointInfo((POS_FIRST_RECORD + position).toBytesPosition())
    }
}