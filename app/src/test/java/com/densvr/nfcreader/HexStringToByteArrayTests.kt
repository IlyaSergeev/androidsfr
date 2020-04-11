package com.densvr.nfcreader

import com.densvr.util.asHex
import com.densvr.util.hexAsByteArray
import org.junit.Test
import kotlin.test.assertEquals

class HexStringToByteArrayTests {

    private fun checkByteArrays(byteArray1: ByteArray, byteArray2: ByteArray) {
        assertEquals(byteArray1.size, byteArray2.size)
        byteArray1.zip(byteArray2).forEach { assertEquals(it.first, it.second) }
    }

    @Test
    fun zeroHexToByteArrayTest() {
        checkByteArrays(byteArrayOf(0, 0, 0), "000000".hexAsByteArray)
    }

    @Test
    fun lowHexToByteArrayTest() {
        checkByteArrays("010203040506070809".hexAsByteArray, byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
        checkByteArrays("0A0C0D0E0F0B".hexAsByteArray, byteArrayOf(10, 12, 13, 14, 15, 11))
    }

    @Test
    fun higHexToByteArrayTest() {
        checkByteArrays(
            "7FF5202C649C2512".hexAsByteArray,
            byteArrayOf(127, -11, 32, 44, 100, -100, 37, 18)
        )
    }

    @Test
    fun reverseConvertUpperTest() {
        val value = "040502987633A9BCD6D0FF"
        assertEquals(value, value.hexAsByteArray.asHex)
    }
}