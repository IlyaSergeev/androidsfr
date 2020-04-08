package com.densvr.nfcreader

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Created by i-sergeev on 07.04.2020.
 */
class SFRInt32ReaderTests {

    @Test
    fun readZeroInt21FromBytesAtZeroPositionTest() {
        val bytes = byteArrayOf(0, 0, 0, 0)
        assertEquals(0, bytes.readInt32Value(0))
    }

    @Test
    fun readZeroInt32FromBytesAtThirdPositionTest() {
        val bytes = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        assertEquals(0, bytes.readInt32Value(3))
    }

    @Test
    fun failReadInt32FromBytesAtPositivePositionTest() {
        val bytes = byteArrayOf(0)
        assertFails("Byte array is to small for read Int32") { bytes.readInt32Value(0) }
    }

    @Test
    fun failReadInt32FromBytesAtNegativePositionTest() {
        val bytes = byteArrayOf(0)
        assertFails("Byte array is to small for read Int32") { bytes.readInt32Value(-1) }
    }

    @Test
    fun readSimpleInt32FromBytesAtZeroPositionTest() {
        assertEquals(1, byteArrayOf(0, 0, 0, 1).readInt32Value(0))
        assertEquals(15, byteArrayOf(0, 0, 0, 15).readInt32Value(0))
        assertEquals(127, byteArrayOf(0, 0, 0, 127).readInt32Value(0))
        assertEquals(255, byteArrayOf(0, 0, 0, -1).readInt32Value(0))
        assertEquals(256, byteArrayOf(0, 0, 1, 0).readInt32Value(0))
        assertEquals(32512, byteArrayOf(0, 0, 127, 0).readInt32Value(0))
        assertEquals(32768, byteArrayOf(0, 0, -128, 0).readInt32Value(0))
        assertEquals(16188, byteArrayOf(0, 0, 63, 60).readInt32Value(0))
    }

    @Test
    fun readSimpleInt32FromBytesAtNotZeroPositionTest() {
        assertEquals(1, byteArrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0).readInt32Value(3))
        assertEquals(15, byteArrayOf(0, 0, 0, 0, 0, 15, 0, 0, 0).readInt32Value(2))
        assertEquals(127, byteArrayOf(0, 0, 0, 0, 127).readInt32Value(1))
        assertEquals(255, byteArrayOf(0, 0, 0, 0, 0, 0, 0, -1).readInt32Value(4))
        assertEquals(256, byteArrayOf(0, 0, 0, 0, 1, 0).readInt32Value(2))
        assertEquals(32512, byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 127, 0, 0, 0).readInt32Value(6))
        assertEquals(32768, byteArrayOf(0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 0).readInt32Value(5))
        assertEquals(16188, byteArrayOf(0, 0, 0, 63, 60).readInt32Value(1))
    }

    @Test
    fun readLargeInt32FromBytesAtZeroPositionTest() {
        assertEquals(16908289, byteArrayOf(1, 2, 0, 1).readInt32Value(0))
        assertEquals(16908303, byteArrayOf(1, 2, 0, 15).readInt32Value(0))
        assertEquals(2013462655, byteArrayOf(120, 3, 0, 127).readInt32Value(0))
        assertEquals(917759, byteArrayOf(0, 14, 0, -1).readInt32Value(0))
        assertEquals(1191182592, byteArrayOf(71, 0, 1, 0).readInt32Value(0))
        assertEquals(303202048, byteArrayOf(18, 18, 127, 0).readInt32Value(0))
        assertEquals(239239168, byteArrayOf(14, 66, -128, 0).readInt32Value(0))
        assertEquals(1845509948, byteArrayOf(110, 0, 63, 60).readInt32Value(0))
    }

    @Test
    fun readLargeInt32FromBytesAtNotZeroPositionTest() {
        assertEquals(16908289, byteArrayOf(0, 0, 1, 2, 0, 1).readInt32Value(2))
        assertEquals(16908303, byteArrayOf(0, 0, 0, 1, 2, 0, 15).readInt32Value(3))
        assertEquals(2013462655, byteArrayOf(0, 0, 0, 0, 0, 120, 3, 0, 127).readInt32Value(5))
        assertEquals(917759, byteArrayOf(0, 0, 14, 0, -1).readInt32Value(1))
        assertEquals(1191182592, byteArrayOf(0, 71, 0, 1, 0).readInt32Value(1))
        assertEquals(303202048, byteArrayOf(0, 0, 18, 18, 127, 0).readInt32Value(2))
        assertEquals(239239168, byteArrayOf(0, 0, 0, 0, 14, 66, -128, 0).readInt32Value(4))
        assertEquals(1845509948, byteArrayOf(0, 0, 0, 0, 0, 0, 110, 0, 63, 60).readInt32Value(6))
    }
}