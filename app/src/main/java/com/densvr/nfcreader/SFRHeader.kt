package com.densvr.nfcreader

import kotlin.math.max

class SFRHeader(
    val lastFormatTime: Long,
    val operationInfo: SFROperationInfo,
    val chipNumber: Int,
    val startPointInfo: SFRPointInfo
) {
    val pointsCount
        get() = max(0, operationInfo.lastPointId - POS_START_STATION)
}