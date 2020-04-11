package com.densvr.nfcreader

class SfrRecord(
    val lastFormatTime: Long,
    val personNumber: Int,
    val cipType: SfrChipType?,
    val startPoint: SFRPointInfo,
    val points: List<SFRPointInfo>
)