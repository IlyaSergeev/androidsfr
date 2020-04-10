package com.densvr.nfcreader

import android.nfc.Tag
import android.nfc.tech.NfcV
import timber.log.Timber

class NfcVReaderTask {

    fun readNfcTag(nfcTag: Tag) {

        try {
            NfcV.get(nfcTag)?.use { nfcV ->

                nfcV.connect()

                val sfrHeader = nfcV.readSFRHeader()

                val pointsCount = sfrHeader?.pointsCount ?: 0
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