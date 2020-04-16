package com.densvr.nfc

import android.nfc.Tag
import android.nfc.tech.NfcV

fun Tag.writeSfrData(tags: Collection<SfrTagBytes>) {

    return NfcV.get(this).use { nfcV ->
        nfcV.connect()

        tags.forEach { tag ->
            nfcV.writeTag(tag.index, tag.bytes)
        }
    }
}

val Tag.canWriteSfrRecord: Boolean
    get() = hasNfcVTech