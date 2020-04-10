package com.densvr.nfcreader

/**
 * Created by i-sergeev on 07.04.2020.
 */
class SFRPointInfo(
    val pointId: Int,
    val time: Long
)

fun SFRPointInfo?.isEmpty(): Boolean {
    return this == null || (pointId == 0 && time == 0L)
}