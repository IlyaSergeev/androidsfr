package com.densvr.nfcreader

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Created by i-sergeev on 07.04.2020.
 */
class SFRInt16ReaderTests {

    @Test
    fun readZeroInt16FromBytesAtZeroPositionTest() {
        val bytes = byteArrayOf(0, 0)
        assertEquals(0, bytes.readInt16Value(0))
    }

    @Test
    fun readZeroInt16FromBytesAtThirdPositionTest() {
        val bytes = byteArrayOf(0, 0, 0, 0, 0)
        assertEquals(0, bytes.readInt16Value(3))
    }

    @Test
    fun failReadInt16FromBytesAtPositivePositionTest() {
        val bytes = byteArrayOf(0)
        assertFails("Byte array is to small for read int16") { bytes.readInt16Value(0) }
    }

    @Test
    fun failReadInt16FromBytesAtNegativePositionTest() {
        val bytes = byteArrayOf(0)
        assertFails("Byte array is to small for read int16") { bytes.readInt16Value(-1) }
    }

    @Test
    fun readSimpleInt16FromBytesAtZeroPositionTest() {
        assertEquals(1, byteArrayOf(0, 1).readInt16Value(0))
        assertEquals(15, byteArrayOf(0, 15).readInt16Value(0))
        assertEquals(127, byteArrayOf(0, 127).readInt16Value(0))
        assertEquals(255, byteArrayOf(0, -1).readInt16Value(0))
        assertEquals(256, byteArrayOf(1, 0).readInt16Value(0))
        assertEquals(32512, byteArrayOf(127, 0).readInt16Value(0))
        assertEquals(32768, byteArrayOf(-128, 0).readInt16Value(0))
        assertEquals(16188, byteArrayOf(63, 60).readInt16Value(0))
    }

    @Test
    fun readSimpleInt16FromBytesAtNotZeroPositionTest() {
        assertEquals(1, byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0).readInt16Value(3))
        assertEquals(15, byteArrayOf(0, 0, 0, 15, 0, 0, 0).readInt16Value(2))
        assertEquals(127, byteArrayOf(0, 0, 127).readInt16Value(1))
        assertEquals(255, byteArrayOf(0, 0, 0, 0, 0, -1).readInt16Value(4))
        assertEquals(256, byteArrayOf(0, 0, 1, 0).readInt16Value(2))
        assertEquals(32512, byteArrayOf(0, 0, 0, 0, 0, 0, 127, 0, 0, 0).readInt16Value(6))
        assertEquals(32768, byteArrayOf(0, 0, 0, 0, 0, -128, 0, 0, 0).readInt16Value(5))
        assertEquals(16188, byteArrayOf(0, 63, 60).readInt16Value(1))
    }
}