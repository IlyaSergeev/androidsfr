package com.densvr.nfcreader

/**
 * Created by i-sergeev on 11.04.2020.
 */
internal const val SRF_BLOCK_SIZE_BYTES = 4 * ONE_BYTE

internal const val SFR_BLOCK_POS_START = 0
internal const val SFR_BLOCK_POS_LAST_FORMAT_TIME = 1
internal const val SFR_BLOCK_POS_CHIP_NUMBER = 3
internal const val SFR_BLOCK_POS_OPERATION = 4
internal const val SFR_BLOCK_POS_BASE_POINT = 5
internal const val SFR_BLOCK_POS_FIRST_POINT = 6

internal val Int.sfrBlockOffset: Int
    get() = this * SRF_BLOCK_SIZE_BYTES