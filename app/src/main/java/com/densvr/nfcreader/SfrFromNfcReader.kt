package com.densvr.nfcreader

import android.nfc.TagLostException
import android.nfc.tech.NfcV
import com.densvr.util.retryOnError
import kotlin.math.max

/**
 * Created by i-sergeev on 10.04.2020.
 */
private val readSfrHeaderCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_MULTIPLE_BLOCK,
    SFR_BLOCK_POS_START.toByte(),
    SFR_BLOCK_POS_BASE_POINT.toByte()
)

private val readSfrPointsWithCountCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_MULTIPLE_BLOCK,
    SFR_BLOCK_POS_FIRST_POINT.toByte(),
    ZERO_BYTE
)

private val readSfrPointCommand = byteArrayOf(
    NFC_HIGH_DATA_RATE_FLAG,
    NFC_READ_SINGLE_BLOCK,
    ZERO_BYTE
)

private const val NFC_READ_TRY_COUNTS = 3

internal fun NfcV.readSFRHeader(): SfrHeader {

    return retryReadNfcVData {
        val sfrHeaderBytes = transceive(readSfrHeaderCommand).also {
            it.logAsNfcMessage(0, "Read SFR Header")
        }

        val responseCode = sfrHeaderBytes.readResponseCode(0)
        if (responseCode == NfcResponseCode.CommandWasSuccessful) {
            sfrHeaderBytes.readSFRHeader(responseCode.length)
        } else {
            throw NfcVReaderException(responseCode)
        }
    }
}

internal fun NfcV.readSFRPointInfoWithCount(pointsCount: Int): List<SFRPointInfo> {

    val points = arrayListOf<SFRPointInfo>()
    var blockSize = pointsCount
    var start = 0
    while (blockSize > 0) {
        try {
            while (start < pointsCount) {
                points += readSFRPointInfoWithCount(
                    start,
                    blockSize - max(0, (start + blockSize) - pointsCount)
                )
                start += blockSize
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

internal fun NfcV.readSFRPointInfoWithCount(position: Int, count: Int): List<SFRPointInfo> {

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
                val pointBytes = transceive(readSfrPointsWithCountCommand).also {
                    it.logAsNfcMessage(
                        position + SFR_BLOCK_POS_FIRST_POINT,
                        "Read SFR points from=$position count=$count"
                    )
                }
                val responseCode = pointBytes.readResponseCode(0)
                if (responseCode == NfcResponseCode.CommandWasSuccessful) {
                    Array(count) { i ->
                        pointBytes.readSFRPointInfo(responseCode.length + i.sfrBlockOffset)
                    }.asList()
                } else {
                    throw NfcVReaderException(responseCode)
                }
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

internal fun NfcV.readSFRPointInfo(position: Int): SFRPointInfo? {

    return retryReadNfcVData {
        readSfrPointCommand[2] = position.toByte()
        val pointBytes = transceive(readSfrPointCommand).also {
            it.logAsNfcMessage(
                position + SFR_BLOCK_POS_FIRST_POINT,
                "Read SFR point at position=${position + SFR_BLOCK_POS_FIRST_POINT}"
            )
        }
        val responseCode = pointBytes.readResponseCode(0)
        if (responseCode == NfcResponseCode.CommandWasSuccessful) {
            pointBytes.readSFRPointInfo(responseCode.length)
        } else {
            null
        }
    }
}

private inline fun <T> retryReadNfcVData(action: () -> T): T {
    return retryOnError(
        NFC_READ_TRY_COUNTS,
        { throwable -> throwable is TagLostException },
        action
    )
}
