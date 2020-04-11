package com.densvr.nfcreader

import com.densvr.util.hexAsByteArray
import org.junit.Test
import kotlin.test.assertEquals

class SFRChipNumberParserTests {

    @Test
    fun parseZeroArrayToPersonNumberTest() {

        assertEquals(0, "00000000".hexAsByteArray.readChipNumber(0))
    }

    @Test
    fun parseOnlyFirstByteArrayToPersonNumberTest() {

        assertEquals(1, "01000000".hexAsByteArray.readChipNumber(0))
        assertEquals(17, "11000000".hexAsByteArray.readChipNumber(0))
        assertEquals(170, "AA000000".hexAsByteArray.readChipNumber(0))
        assertEquals(15, "0F000000".hexAsByteArray.readChipNumber(0))
    }

    @Test
    fun parseOnlySecondByteArrayToPersonNumberTest() {

        assertEquals(200 * 1, "00010000".hexAsByteArray.readChipNumber(0))
        assertEquals(200 * 17, "00110000".hexAsByteArray.readChipNumber(0))
        assertEquals(200 * 170, "00AA0000".hexAsByteArray.readChipNumber(0))
        assertEquals(200 * 15, "000F0000".hexAsByteArray.readChipNumber(0))
    }

    @Test
    fun parseOnlyThirdByteArrayToPersonNumberTest() {

        assertEquals(40000 * 1, "00000100".hexAsByteArray.readChipNumber(0))
        assertEquals(40000 * 17, "00001100".hexAsByteArray.readChipNumber(0))
        assertEquals(40000 * 170, "0000AA00".hexAsByteArray.readChipNumber(0))
        assertEquals(40000 * 15, "00000F00".hexAsByteArray.readChipNumber(0))
    }

    @Test
    fun parseOnlyLastByteArrayToPersonNumberTest() {

        assertEquals(0, "00000001".hexAsByteArray.readChipNumber(0))
        assertEquals(0, "00000011".hexAsByteArray.readChipNumber(0))
        assertEquals(0, "000000AA".hexAsByteArray.readChipNumber(0))
        assertEquals(0, "0000000F".hexAsByteArray.readChipNumber(0))
    }

    @Test
    fun parseComplexByteArrayToPersonNumberTest() {

        assertEquals(8197570, "AABBCC00".hexAsByteArray.readChipNumber(0))
        assertEquals(10222819, "1372FF00".hexAsByteArray.readChipNumber(0))
        assertEquals(1667314, "72882900".hexAsByteArray.readChipNumber(0))
        assertEquals(2126552, "98203500".hexAsByteArray.readChipNumber(0))

        assertEquals(8197570, "AABBCC12".hexAsByteArray.readChipNumber(0))
        assertEquals(10222819, "1372FF73".hexAsByteArray.readChipNumber(0))
        assertEquals(1667314, "728829FF".hexAsByteArray.readChipNumber(0))
        assertEquals(2126552, "98203597".hexAsByteArray.readChipNumber(0))
    }

    @Test
    fun parseArrayFromDocToPersonNumberTest() {

        assertEquals(231, "1F010000".hexAsByteArray.readChipNumber(0))
    }
}