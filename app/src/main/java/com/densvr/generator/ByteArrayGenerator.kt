package com.densvr.generator

import com.densvr.nfcreader.SFR_HEAD_SIZE_BYTES
import com.densvr.nfcreader.SRF_BLOCK_SIZE_BYTES
import kotlin.random.Random

fun Random.nextSfrRecordBytes(): ByteArray {
    val recordsCount = nextInt(122)
    val bytesCount = nextInt(SFR_HEAD_SIZE_BYTES, SFR_HEAD_SIZE_BYTES + recordsCount)
    return nextBytes(bytesCount * SRF_BLOCK_SIZE_BYTES)
}