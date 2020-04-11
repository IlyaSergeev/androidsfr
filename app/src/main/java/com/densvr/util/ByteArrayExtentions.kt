package com.densvr.util

/**
 * Created by i-sergeev on 11.04.2020.
 */
fun areEquals(
    byteArray1: ByteArray, offset1: Int, length1: Int,
    byteArray2: ByteArray, offset2: Int, length2: Int
): Boolean {

    return if (length1 == length2) {
        for (i in 0 until length1) {
            if (byteArray1[offset1 + i] != byteArray2[offset2 + i]) {
                return false
            }
        }
        true
    } else {
        false
    }
}