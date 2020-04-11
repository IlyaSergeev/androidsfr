package com.densvr.util

import java.util.*

/**
 * Created by i-sergeev on 11.04.2020.
 */
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