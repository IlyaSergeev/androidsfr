package com.densvr.util

import com.densvr.nfc.uint

/**
 * Created by i-sergeev on 11.04.2020.
 */
val String.hexAsByteArray
    //TODO .toByte() is overhead
    inline get() = chunked(2).map { it.toUByte(16).toByte() }.toByteArray()

val ByteArray.asHex
    inline get() = this.joinToString(separator = "") {
        "%02X".format(it.uint)
    }

fun ByteArray.asHex(offset: Int, length: Int): String {
    return Array(length) { i ->
        "%02X".format(this[i + offset].uint)
    }.joinToString(separator = "")
}