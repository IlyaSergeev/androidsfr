package com.densvr.nfc

import kotlin.math.max

class SfrHeader(
    val lastFormatTime: Long,
    val operationInfo: SfrOperationInfo,
    val chipNumber: Int,
    val startPointInfo: SFRPointInfo
) {
    val pointsCount
        get() = max(0, operationInfo.lastRecordPosition - SFR_BLOCK_POS_BASE_POINT)
}