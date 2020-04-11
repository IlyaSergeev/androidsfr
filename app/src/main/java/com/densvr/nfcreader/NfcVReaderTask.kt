package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV

fun Tag.readSfrRecord(): SFRRecord {

    return NfcV.get(this).use { nfcV ->

        nfcV.connect()

        val sfrHeader = nfcV.readSFRHeader()

        val pointsCount = sfrHeader.pointsCount ?: 0
        val sfrPoints = if (pointsCount > 0) {
            nfcV.readSFRPointInfoWithCount(pointsCount)
        } else {
            nfcV.readAllSFRPointInfo()
        }
        SFRRecord(
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