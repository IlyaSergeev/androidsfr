package com.densvr.nfcreader

import org.junit.Test
import kotlin.test.assertEquals

class ByteArrayToHexStringTests {

    @Test
    fun zeroArrayToHexUpperStringTest() {
        assertEquals("000000", byteArrayOf(0, 0, 0).asHexUpper)
    }

    @Test
    fun zeroArrayToHexLowerStringTest() {
        assertEquals("00000000", byteArrayOf(0, 0, 0, 0).asHexLower)
    }

    @Test
    fun lowArrayToHexUpperStringTest() {
        assertEquals("010203040506070809", byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9).asHexUpper)
        assertEquals("0A0C0D0E0F0B", byteArrayOf(10, 12, 13, 14, 15, 11).asHexUpper)
    }

    @Test
    fun lowArrayToHexLowerStringTest() {
        assertEquals("04070901020103050608", byteArrayOf(4, 7, 9, 1, 2, 1, 3, 5, 6, 8).asHexLower)
        assertEquals("0e0c0b0a0f0d", byteArrayOf(14, 12, 11, 10, 15, 13).asHexLower)
    }

    @Test
    fun highArrayToHexUpperStringTest() {
        assertEquals("7FF5202C649C2512", byteArrayOf(127, -11, 32, 44, 100, -100, 37, 18).asHexUpper)
    }

    @Test
    fun highArrayToHexLowerStringTest() {
        assertEquals("7ff5202c649c2512", byteArrayOf(127, -11, 32, 44, 100, -100, 37, 18).asHexLower)
    }
}