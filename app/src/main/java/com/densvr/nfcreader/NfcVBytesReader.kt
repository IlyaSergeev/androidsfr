package com.densvr.nfcreader

import android.nfc.TagLostException
import android.nfc.tech.NfcV
import com.densvr.utility.retryOnError
import timber.log.Timber

/**
 * Created by i-sergeev on 10.04.2020.
 */
private val readSfrHeaderCommand = byteArrayOf(0x02, 0x23, 0x00, 0x05)
private val readSfrPointsWithCountCommand = byteArrayOf(0x02, 0x23, 0x06, 0x00)
private val readSfrPointCommand = byteArrayOf(0x02, 0x20, 0x00)

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

private const val NFC_READ_TRY_COUNTS = 3

internal fun NfcV.readSFRHeader(): SFRHeader? {

    return retryReadNfcVData {
        val sfrHeaderBytes = transceive(readSfrHeaderCommand).also {
            it.logAsTagTable(0, "Read SFR Header")
        }

        val nfcVParser = NfcVResponseParser(sfrHeaderBytes, 0, sfrHeaderBytes.size)
        val responseCode = nfcVParser.responseCode
        if (responseCode.isSuccessful) {
            SFRHeaderParser(nfcVParser.bytes, responseCode.length).sfrHeader
        } else {
            throw NfcVReaderException(responseCode)
        }
    }
}

internal fun NfcV.readSFRPointInfoWithCount(pointsCount: Int): List<SFRPointInfo> {

    return retryReadNfcVData {
        readSfrPointsWithCountCommand[3] = pointsCount.toByte()
        val pointBytes = transceive(readSfrPointsWithCountCommand).also {
            it.logAsTagTable(
                SFRParser.POS_FIRST_RECORD,
                "Read SFR all points count=$pointsCount"
            )
        }
        val responseParser = NfcVResponseParser(pointBytes, 0, pointBytes.size)
        val responseCode = responseParser.responseCode
        if (responseCode.isSuccessful) {
            Array(pointsCount) { i ->
                pointBytes.readSFRPointInfo(responseCode.length + i * SFR_BLOCK_SIZE_BITES)
            }.asList()
        } else {
            emptyList()
        }
    }
}

internal fun NfcV.readAllSFRPointInfo(): List<SFRPointInfo> {
    return arrayListOf<SFRPointInfo>().also { points ->
        var nextPoint: SFRPointInfo?
        var position = POS_FIRST_POINT_POSITION
        do {
            nextPoint = readSFRPointInfo(position)?.also {
                points += it
            }
            position++
        } while (!nextPoint.isEmpty())
    }
}

internal fun NfcV.readSFRPointInfo(position: Int): SFRPointInfo? {

    return retryReadNfcVData {
        readSfrPointCommand[2] = position.toByte()
        val pointBytes = transceive(readSfrPointCommand).also {
            it.logAsTagTable(
                position,
                "Read SFR point at position=$position"
            )
        }
        val responseParser = NfcVResponseParser(pointBytes, 0, pointBytes.size)
        val responseCode = responseParser.responseCode
        if (responseCode.isSuccessful) {
            pointBytes.readSFRPointInfo(responseCode.length)
        } else {
            null
        }
    }
}

internal fun ByteArray.readResponseCode(offset: Int): NfcVResponseCode {

    val operationSize = kotlin.math.max(0, size - offset)
    return if (operationSize == 0) {
        NfcVResponseCode.NoStatusInformation
    } else {
        NfcVResponseCode.values().firstOrNull() {
            if (it.bytes.isNotEmpty()) {
                val compareBytesCount = kotlin.math.min(it.bytes.size, operationSize)
                areEqualByteArrays(
                    this, offset, compareBytesCount,
                    it.bytes, 0, compareBytesCount
                )
            } else {
                false
            }
        } ?: NfcVResponseCode.Unknown
    }
}

private inline fun <T> retryReadNfcVData(action: () -> T): T {
    return retryOnError(
        NFC_READ_TRY_COUNTS,
        { throwable -> throwable is TagLostException },
        action
    )
}
