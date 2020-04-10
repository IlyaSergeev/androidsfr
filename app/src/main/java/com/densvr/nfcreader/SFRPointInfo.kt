package com.densvr.nfcreader

/**
 * Created by i-sergeev on 07.04.2020.
 */
class SFRPointInfo(
    val pointId: Int,
    val time: Long
)

internal val emptySFRPointInfo = SFRPointInfo(0, 0)

fun SFRPointInfo?.isEmpty(): Boolean {
    return this == null ||
            (pointId == emptySFRPointInfo.pointId && time == emptySFRPointInfo.time)
}