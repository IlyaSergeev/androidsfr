package com.densvr.mock

import com.densvr.nfcreader.*
import java.lang.System.currentTimeMillis
import kotlin.random.Random

fun Random.nextSfrRecordBytes(): ByteArray {
    val recordsCount = nextInt(122)
    val bytesCount = if (recordsCount > 0) {
        nextInt(SFR_HEAD_SIZE_BLOCKS, SFR_HEAD_SIZE_BLOCKS + recordsCount)
    } else {
        SFR_HEAD_SIZE_BLOCKS
    }
    return nextBytes(bytesCount * SRF_BLOCK_SIZE_BYTES)
}

fun Random.nextSfrRecord(): SfrRecord {
    return SfrRecord(
        currentTimeMillis(),
        100500,
        SfrChipType.COMPETITOR,
        SFRPointInfo(42, 0),
        listOf(
            SFRPointInfo(13, 100),
            SFRPointInfo(14, 200),
            SFRPointInfo(15, 300),
            SFRPointInfo(16, 400),
            SFRPointInfo(13, 500),
            SFRPointInfo(14, 600),
            SFRPointInfo(15, 700),
            SFRPointInfo(23, 800),
            SFRPointInfo(33, 900),
            SFRPointInfo(43, 1000)
        )
    )
}