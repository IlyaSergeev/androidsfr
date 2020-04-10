package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV
import timber.log.Timber

class NfcVReaderTask {

    companion object {
        private val readSfrHeaderCommand = byteArrayOf(0x02, 0x23, 0x00, 0x05)
        private val readSfrPointsWithCountCommand = byteArrayOf(0x02, 0x23, 0x06, 0x00)
        private val readSfrPointCommand = byteArrayOf(0x02, 0x22, 0x00)

        private fun ByteArray.logAsTagTable(startTagNumber: Int, operation: String) {

            Timber.tag("NFC Reader").d(operation)

            val responseString = asNumiratedString(
                "h: ",
                8,
                startTagNumber,
                "\n"
            )
            Timber.tag("NFC Reader")
                .d("header:\n${responseString}")
        }

        private fun NfcV.readSFRHeader(): SFRHeader {

            val sfrHeaderBytes = transceive(readSfrHeaderCommand).also {
                it.logAsTagTable(0, "Read SFR Header")
            }

            val nfcVParser = NfcVResponseParser(sfrHeaderBytes, 0, sfrHeaderBytes.size)
            val responseCode = nfcVParser.responseCode
            return if (responseCode?.isSuccessful == true) {
                SFRHeaderParser(nfcVParser.bytes, responseCode.length).sfrHeader
            } else {
                throw IllegalAccessError("Can not read SRF header data from NFC")
            }
        }

        private fun NfcV.readSFRPointInfoWithCount(pointsCount: Int): List<SFRPointInfo> {

            readSfrPointsWithCountCommand[3] = pointsCount.toByte()
            val pointBytes = transceive(readSfrPointsWithCountCommand).also {
                it.logAsTagTable(
                    SFRParser.POS_FIRST_RECORD,
                    "Read SFR all points count=$pointsCount"
                )
            }
            val responseParser = NfcVResponseParser(pointBytes, 0, pointBytes.size)
            val responseCode = responseParser.responseCode
            return if (responseCode?.isSuccessful == true) {
                Array(pointsCount) { i ->
                    pointBytes.readSFRPointInfo(responseCode.length + i * SFR_BLOCK_SIZE_BITES)
                }.asList()
            } else {
                throw IllegalAccessError("Can not read SRF points wih count=$pointsCount from NFC")
            }
        }

        private fun NfcV.readAllSFRPointInfo(): List<SFRPointInfo> {
            return arrayListOf<SFRPointInfo>().also { points ->
                var nextPoint: SFRPointInfo?
                var position = 0
                do {
                    nextPoint = readSFRPointInfo(position)?.also {
                        points += it
                    }
                    position++
                } while (nextPoint != null)
            }
        }

        private fun NfcV.readSFRPointInfo(position: Int): SFRPointInfo? {
            return try {
                readSfrPointCommand[2] = position.toByte()
                val pointBytes = transceive(readSfrPointCommand).also {
                    it.logAsTagTable(
                        SFRParser.POS_FIRST_RECORD,
                        "Read SFR point at position=$position"
                    )
                }
                val responseParser = NfcVResponseParser(pointBytes, 0, pointBytes.size)
                val responseCode = responseParser.responseCode
                if (responseCode?.isSuccessful == true && !pointBytes.isEmptySFRBlock(
                        responseCode.length
                    )
                ) {
                    pointBytes.readSFRPointInfo(responseCode.length)
                } else {
                    null
                }
            } catch (error: Throwable) {
                null
            }
        }
    }

    fun readNfcTag(nfcTag: Tag) {

        try {
            NfcV.get(nfcTag)?.use { nfcV ->

                nfcV.connect()

                val sfrHeader = nfcV.readSFRHeader()

                val pointsCount = sfrHeader.pointsCount
                val sfrPoints = if (pointsCount > 0) {
                    nfcV.readSFRPointInfoWithCount(pointsCount)
                } else {
                    nfcV.readAllSFRPointInfo()
                }
            }
        } catch (error: Throwable) {
            Timber.tag("NFC Reader").e(error)
        }
    }
}