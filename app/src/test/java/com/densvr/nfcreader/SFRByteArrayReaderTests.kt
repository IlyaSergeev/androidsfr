package com.densvr.nfcreader

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Created by i-sergeev on 07.04.2020.
 */
class SFRByteArrayReaderTests {

    @Test
    fun readZeroInt16FromBytesAtStartPositionTest() {
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
}