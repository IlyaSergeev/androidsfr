package com.densvr.nfcreader

import android.nfc.TagLostException
import android.nfc.tech.NfcV
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

internal fun NfcV.readSFRHeader(): SfrHeader {

    return retryReadNfcVData {
        transceive(readSfrHeaderCommand).also {
            it.logAsNfcMessage(0, "Read SFR Header")
        }.parseNfcMessage(
            { bytes, offset -> bytes.readSFRHeader(offset) },
            { responseCode -> throw NfcVReaderException(responseCode) }
        )
    }
}

internal fun NfcV.readSFRPointInfoInRange(pointsCount: Int): List<SFRPointInfo> {

    val points = arrayListOf<SFRPointInfo>()
    var blockSize = pointsCount
    var startPosition = 0
    while (blockSize > 0) {
        try {
            while (startPosition < pointsCount) {
                points += readSFRPointInfoInRange(
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
    return readAllSFRPointInfo()
}


private val readSfrPointsWithCountCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_MULTIPLE_BLOCK,
    SFR_BLOCK_POS_FIRST_POINT.toByte(),
    ZERO_BYTE
)

internal fun NfcV.readSFRPointInfoInRange(position: Int, count: Int): List<SFRPointInfo> {

    return if (count == 1) {
        listOf(readSFRPointInfo(position) ?: emptySFRPointInfo)
    } else {
        retryOnError(
            NFC_READ_TRY_COUNTS,
            { throwable ->
                throwable is TagLostException || (throwable is NfcVReaderException && throwable.responseCode != NfcResponseCode.UnknownError)
            },
            {
                readSfrPointsWithCountCommand[2] = (SFR_BLOCK_POS_FIRST_POINT + position).toByte()
                readSfrPointsWithCountCommand[3] = (count - 1).toByte()

                transceive(readSfrPointsWithCountCommand).also {
                    it.logAsNfcMessage(
                        position + SFR_BLOCK_POS_FIRST_POINT,
                        "Read SFR points from=$position count=$count"
                    )
                }.parseNfcMessage(
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

internal fun NfcV.readAllSFRPointInfo(): List<SFRPointInfo> {
    return arrayListOf<SFRPointInfo>().also { points ->
        var nextPoint: SFRPointInfo?
        var position = SFR_BLOCK_POS_FIRST_POINT
        do {
            nextPoint = readSFRPointInfo(position)?.also {
                points += it
            }
            position++
        } while (!nextPoint.isEmpty())
    }
}


private val readSfrPointCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_SINGLE_BLOCK,
    ZERO_BYTE
)

internal fun NfcV.readSFRPointInfo(position: Int): SFRPointInfo? {

    return retryReadNfcVData {

        readSfrPointCommand[2] = position.toByte()

        transceive(readSfrPointCommand).also {
            it.logAsNfcMessage(
                position + SFR_BLOCK_POS_FIRST_POINT,
                "Read SFR point at position=${position + SFR_BLOCK_POS_FIRST_POINT}"
            )
        }.parseNfcMessage(
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