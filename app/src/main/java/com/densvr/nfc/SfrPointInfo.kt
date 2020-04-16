package com.densvr.nfc

import java.util.concurrent.TimeUnit

/**
 * Created by i-sergeev on 07.04.2020.
 */
class SFRPointInfo(
    val pointId: Int,
    val timeSeconds: Long
) {
    val timeMillis = TimeUnit.SECONDS.toMillis(timeSeconds)
}

internal val emptySFRPointInfo = SFRPointInfo(0, 0)

fun SFRPointInfo?.isEmpty(): Boolean {
    return this == null ||
            (pointId == emptySFRPointInfo.pointId && timeSeconds == emptySFRPointInfo.timeSeconds)
}