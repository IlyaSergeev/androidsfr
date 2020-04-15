package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV
import com.densvr.util.BytesLogger

fun Tag.readSfrRecord(bytesLogger: BytesLogger): SfrRecord {

    return NfcV.get(this).use { nfcV ->

        nfcV.connect()

        val sfrHeader = nfcV.readSFRHeader(bytesLogger)

        val pointsCount = sfrHeader.pointsCount
        val sfrPoints = if (pointsCount > 0) {
            nfcV.readSFRPointInfoInRange(bytesLogger, pointsCount)
        } else {
            nfcV.readAllSFRPointInfo(bytesLogger)
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