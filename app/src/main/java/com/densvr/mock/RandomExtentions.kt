package com.densvr.mock

import com.densvr.nfcreader.SFR_HEAD_SIZE_BLOCKS
import com.densvr.nfcreader.SRF_BLOCK_SIZE_BYTES
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