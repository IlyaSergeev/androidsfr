package com.densvr.nfcreader

import java.util.*

val String.hexAsByteArray
    inline get() = chunked(2).map { it.toUpperCase(Locale.ROOT).toInt(16).toByte() }.toByteArray()

val ByteArray.asHex
    inline get() = this.joinToString(separator = "") {
        "%02X".format(it.toInt() and 0xFF)
    }

fun ByteArray.asHex(offset: Int, length: Int): String {
    return Array(length - offset) { i ->
        "%02X".format(this[i + offset].toInt() and 0xFF)
    }.joinToString(separator = "")
}

fun ByteArray.asNumiratedString(chankLength: Int, firstIndex: Int, separator: String): String {
    return asHex.chunked(chankLength)
        .mapIndexed { index, hexString -> "${firstIndex + index}: $hexString" }
        .joinToString("\n")
}