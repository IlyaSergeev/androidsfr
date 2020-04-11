package com.densvr.nfcreader

class SFRRecord(
    val lastFormatTime: Long,
    val personNumber: Int,
    val cipType: SFRChipType?,
    val startPoint: SFRPointInfo,
    val points: List<SFRPointInfo>
)