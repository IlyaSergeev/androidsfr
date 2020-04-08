package com.densvr.nfcreader

import java.util.*

val String.hexAsByteArray
    inline get() = chunked(2).map { it.toUpperCase(Locale.ROOT).toInt(16).toByte() }.toByteArray()

val ByteArray.asHexLower
    inline get() = this.joinToString(separator = "") {
        String.format(
            "%02x",
            (it.toInt() and 0xFF)
        )
    }
val ByteArray.asHexUpper
    inline get() = this.joinToString(separator = "") {
        String.format(
            "%02X",
            (it.toInt() and 0xFF)
        )
    }