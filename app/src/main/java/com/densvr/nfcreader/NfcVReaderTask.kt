package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV
import timber.log.Timber

class NfcVReaderTask {

    fun readNfcTag(nfcTag: Tag) {

        try {
            NfcV.get(nfcTag)?.use { nfcV ->

                nfcV.connect()
                val headerCommand = byteArrayOf(0x02, 0x23, 0x00, 0x05)
                val sfrHeaderBytes = nfcV.transceive(headerCommand)
                val sfrHeaderParser = SFRParserHeader(sfrHeaderBytes, 2, sfrHeaderBytes.size - 2)
                Timber.tag("NFC Reader")
                    .d("header:\n${sfrHeaderBytes.asNumiratedString(2, 8, 0, "\n")}")


                val lastPointPosition = sfrHeaderParser.operationInfo.lastRecordPosition
                val pointsCount = 200 // max(0, lastPointPosition - 0x05).toByte()
                if (pointsCount > 0) {
                    val pointsCommand =
                        byteArrayOf(0x02, 0x23, 0x7F, 122.toByte())
                    val pointsBytes = nfcV.transceive(pointsCommand)
                    val sfrPointsParser = SFRParser(pointsBytes, 0, pointsBytes.size)
                    Timber.tag("NFC Reader").d("points:\n${pointsBytes.asNumiratedString(0, 8, 6, "\n")}")
                }

//                for (i in 0 until 122) {
//                    headerCommand[2] = i.toByte()
//                    val result = nfcV.transceive(headerCommand)
//                    Timber.tag("NFC Reader").d("$i: ${result.asHex}")
//                }
            }
        } catch (error: Throwable) {
            Timber.tag("NFC Reader").e(error)
        }
    }
}