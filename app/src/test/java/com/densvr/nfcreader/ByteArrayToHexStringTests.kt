package com.densvr.nfcreader

import org.junit.Test
import kotlin.test.assertEquals

class ByteArrayToHexStringTests {

    @Test
    fun zeroArrayToHexStringTest() {
        assertEquals("000000", byteArrayOf(0, 0, 0).asHex)
    }

    @Test
    fun lowArrayToHexStringTest() {
        assertEquals("010203040506070809", byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9).asHex)
        assertEquals("0A0C0D0E0F0B", byteArrayOf(10, 12, 13, 14, 15, 11).asHex)
    }

    @Test
    fun highArrayToHexUpperStringTest() {
        assertEquals("7FF5202C649C2512", byteArrayOf(127, -11, 32, 44, 100, -100, 37, 18).asHex)
    }
}