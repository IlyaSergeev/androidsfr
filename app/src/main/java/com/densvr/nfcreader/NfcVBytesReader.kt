package com.densvr.nfcreader

import android.nfc.tech.NfcV
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

internal fun NfcV.readSFRHeader(): SFRHeader {

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

internal fun NfcV.readSFRPointInfoWithCount(pointsCount: Int): List<SFRPointInfo> {

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

internal fun NfcV.readAllSFRPointInfo(): List<SFRPointInfo> {
    return arrayListOf<SFRPointInfo>().also { points ->
        var nextPoint: SFRPointInfo?
        var position = POS_FIRST_POINT_POSITION
        do {
            nextPoint = readSFRPointInfo(position)?.also {
                points += it
            }
            position++
        } while (nextPoint != null)
    }
}

internal fun NfcV.readSFRPointInfo(position: Int): SFRPointInfo? {
    return try {
        readSfrPointCommand[2] = position.toByte()
        val pointBytes = transceive(readSfrPointCommand).also {
            it.logAsTagTable(
                position,
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
        Timber.tag("NFC Reader").e(error)
        null
    }
}

private fun NfcV.tranceiveWithRetry(command: ByteArray, tryCount: Int): ByteArray {
    var lastError: Throwable? = null
    repeat(tryCount) {
        try {
            val pointBytes = transceive(readSfrPointCommand)
            val responseParser = NfcVResponseParser(pointBytes, 0, pointBytes.size)
            val responseCode = responseParser.responseCode
            if (responseCode?.isSuccessful == true) {
                return pointBytes
            }
        } catch (error: Throwable) {
            lastError = error
        }
    }
    throw lastError!!
}

internal fun ByteArray.readResponseCode(offset: Int): NfcVResponseCode? {

    val operationSize = kotlin.math.max(0, size - offset)
    return if (operationSize == 0) {
        NfcVResponseCode.NoStatusInformation
    } else {
        NfcVResponseCode.values().firstOrNull {
            if (it.bytes.isNotEmpty()) {
                val compareBytesCount = kotlin.math.min(it.bytes.size, operationSize)
                areEqualByteArrays(
                    this, offset, compareBytesCount,
                    it.bytes, 0, compareBytesCount
                )
            } else {
                false
            }
        }
    }
}