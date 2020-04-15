package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV
import com.densvr.util.SfrReaderLogger

fun Tag.readSfrRecord(readerLogger: SfrReaderLogger): SfrRecord {

    return NfcV.get(this).use { nfcV ->

        nfcV.connect()

        val sfrHeader = nfcV.readSFRHeader(readerLogger)

        val pointsCount = sfrHeader.pointsCount
        val sfrPoints = if (pointsCount > 0) {
            nfcV.readSFRPointInfoInRange(readerLogger, pointsCount)
        } else {
            nfcV.readAllSFRPointInfo(readerLogger)
        }
        SfrRecord(
            sfrHeader.lastFormatTime,
            sfrHeader.chipNumber,
            sfrHeader.operationInfo.type,
            sfrHeader.startPointInfo,
            sfrPoints
        )
    }
}

val Tag.canReadSfrRecord: Boolean
    get() = techList.contains(NfcV::class.java.name)