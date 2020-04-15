package com.densvr.nfcreader

import android.nfc.TagLostException
import android.nfc.tech.NfcV
import com.densvr.util.SfrReaderLogger
import com.densvr.util.retryOnError
import kotlin.math.max

/**
 * Created by i-sergeev on 10.04.2020.
 */
private const val NFC_READ_TRY_COUNTS = 3

private val readSfrHeaderCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_MULTIPLE_BLOCK,
    SFR_BLOCK_POS_START.toByte(),
    SFR_BLOCK_POS_BASE_POINT.toByte()
)

internal fun NfcV.readSFRHeader(readerLogger: SfrReaderLogger): SfrHeader {

    return retryReadNfcVData {
        transceive(readSfrHeaderCommand).also {
            it.logAsNfcMessage(0, "Read SFR Header")
        }.parseNfcMessage(
            readerLogger,
            0,
            { bytes, offset -> bytes.readSFRHeader(offset) },
            { responseCode -> throw NfcVReaderException(responseCode) }
        )
    }
}

internal fun NfcV.readSFRPointInfoInRange(
    readerLogger: SfrReaderLogger,
    pointsCount: Int
): List<SFRPointInfo> {

    val points = arrayListOf<SFRPointInfo>()
    var blockSize = pointsCount
    var startPosition = 0
    while (blockSize > 0) {
        try {
            while (startPosition < pointsCount) {
                points += readSFRPointInfoInRange(
                    readerLogger,
                    startPosition,
                    blockSize - max(0, (startPosition + blockSize) - pointsCount)
                )
                startPosition += blockSize
                blockSize *= 2
            }
            return points
        } catch (error: NfcVReaderException) {
            if (error.responseCode == NfcResponseCode.UnknownError) {
                blockSize /= 2
            } else {
                throw error
            }
        }
    }
    return readAllSFRPointInfo(readerLogger)
}


private val readSfrPointsWithCountCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_MULTIPLE_BLOCK,
    SFR_BLOCK_POS_FIRST_POINT.toByte(),
    ZERO_BYTE
)

internal fun NfcV.readSFRPointInfoInRange(
    readerLogger: SfrReaderLogger,
    position: Int,
    count: Int
): List<SFRPointInfo> {

    return if (count == 1) {
        listOf(readSFRPointInfo(readerLogger, position) ?: emptySFRPointInfo)
    } else {
        val blockPosition = position + SFR_BLOCK_POS_FIRST_POINT
        retryOnError(
            NFC_READ_TRY_COUNTS,
            { throwable ->
                throwable is TagLostException || (throwable is NfcVReaderException && throwable.responseCode != NfcResponseCode.UnknownError)
            },
            {
                readSfrPointsWithCountCommand[2] = (blockPosition).toByte()
                readSfrPointsWithCountCommand[3] = (count - 1).toByte()

                transceive(readSfrPointsWithCountCommand).also {
                    it.logAsNfcMessage(
                        blockPosition,
                        "Read SFR points from=${blockPosition} count=$count"
                    )
                }.parseNfcMessage(
                    readerLogger,
                    blockPosition,
                    { bytes, offset ->
                        Array(count) { i ->
                            bytes.readSFRPointInfo(offset + i.sfrBlockOffset)
                        }.asList()
                    },
                    { responseCode -> throw NfcVReaderException(responseCode) }
                )
            }
        )
    }
}

internal fun NfcV.readAllSFRPointInfo(readerLogger: SfrReaderLogger): List<SFRPointInfo> {
    return arrayListOf<SFRPointInfo>().also { points ->
        var nextPoint: SFRPointInfo?
        var position = 0
        do {
            nextPoint = readSFRPointInfo(readerLogger, position)?.also {
                points += it
            }
            position++
        } while (nextPoint != null)
    }
}


private val readSfrPointCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_SINGLE_BLOCK,
    ZERO_BYTE
)

internal fun NfcV.readSFRPointInfo(readerLogger: SfrReaderLogger, position: Int): SFRPointInfo? {

    val blockPosition = position + SFR_BLOCK_POS_FIRST_POINT
    return retryReadNfcVData {

        readSfrPointCommand[2] = (blockPosition).toByte()

        transceive(readSfrPointCommand).also {
            it.logAsNfcMessage(
                blockPosition,
                "Read SFR point at position=${blockPosition}"
            )
        }.parseNfcMessage(
            readerLogger,
            blockPosition,
            { bytes, offset -> bytes.readSFRPointInfo(offset) },
            { null }
        )
    }
}

private inline fun <T> retryReadNfcVData(action: () -> T): T {
    return retryOnError(
        NFC_READ_TRY_COUNTS,
        { throwable -> throwable is TagLostException },
        action
    )
}

private inline fun <T> ByteArray.parseNfcMessage(
    readerLogger: SfrReaderLogger,
    positionIndex: Int,
    onSuccess: (bytes: ByteArray, offset: Int) -> T,
    onFail: (NfcResponseCode) -> T
): T {
    val responseCode = readResponseCode(0)
    return if (responseCode == NfcResponseCode.CommandWasSuccessful) {
        readerLogger.appendBytes(
            this,
            responseCode.length,
            size - responseCode.length,
            positionIndex
        )
        onSuccess(this, responseCode.length)
    } else {
        readerLogger.appendError(responseCode.errorMessage)
        onFail(responseCode)
    }
}