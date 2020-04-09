package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV
import timber.log.Timber

class NfcVReaderTask {

    fun readNfcTag(nfcTag: Tag) {

        try {
            NfcV.get(nfcTag)?.use { nfcV ->

                nfcV.connect()
                val headerCommand = byteArrayOf(0x02, 0x23, 0x00, 0x06)
                val sfrHeaderBytes = nfcV.transceive(headerCommand)
                val sfrHeaderParser = SFRParserHeader(sfrHeaderBytes, 0, sfrHeaderBytes.size)
                Timber.tag("NFC Reader")
                    .d("header:\n${sfrHeaderBytes.asNumiratedString(8, 0, "\n")}")


                val lastPointPosition = sfrHeaderParser.operationInfo.lastRecordPosition
                val pointsCommand =
                    byteArrayOf(0x02, 0x23, 0x05, 0x05, (lastPointPosition - 0x05).toByte())
                val pointsBytes = nfcV.transceive(pointsCommand)
                val sfrPointsParser = SFRParser(pointsBytes, 0, pointsBytes.size)
                Timber.tag("NFC Reader").d("points: ${pointsBytes.asHex}")


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