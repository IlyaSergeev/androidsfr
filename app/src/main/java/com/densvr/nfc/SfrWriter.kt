package com.densvr.nfc

import android.nfc.tech.NfcV
import com.densvr.util.retryOnError

//TODO write nfc loginc  work unstable on all my devices
private const val NFC_WRITE_TRY_COUNTS = 3

internal const val NFC_WRITE_BLOCK_FLAG = 0x42.toByte()

private const val NFC_WRITE_TAG_INDEX_POSITION = 2
private const val NFC_WRITE_TAG_BLOCK_START_POSITION = 2

private val writeSfrTagCommand =
    arrayListOf<Byte>().apply {
        this += NFC_WRITE_BLOCK_FLAG
        this += NFC_WRITE_SINGLE_BLOCK
        this += ZERO_BYTE
        for (i in 0 until SRF_BLOCK_SIZE_BYTES) {
            this += ZERO_BYTE
        }
    }.let {
        ByteArray(it.size) { i -> it[i] }
    }

fun NfcV.writeTag(tagIndex: Byte, bytes: ByteArray) {

    writeSfrTagCommand[NFC_WRITE_TAG_INDEX_POSITION] = tagIndex
    bytes.copyInto(
        writeSfrTagCommand,
        NFC_WRITE_TAG_BLOCK_START_POSITION,
        0,
        SRF_BLOCK_SIZE_BYTES
    )

    return retryOnError(
        NFC_WRITE_TRY_COUNTS,
        { true },
        {
            transceive(writeSfrTagCommand).also {
                it.logAsNfcWriteMessage(tagIndex, writeSfrTagCommand, "Write single tag ")
            }
                .parseNfcMessage(
                    { _, _ -> },
                    { }
                )
        }
    )
}

private inline fun <T> ByteArray.parseNfcMessage(
    onSuccess: (bytes: ByteArray, offset: Int) -> T,
    onFail: (NfcResponseCode) -> T
): T {
    val responseCode = readResponseCode(0)
    return if (responseCode == NfcResponseCode.CommandWasSuccessful) {
        onSuccess(this, responseCode.length)
    } else {
        onFail(responseCode)
    }
}