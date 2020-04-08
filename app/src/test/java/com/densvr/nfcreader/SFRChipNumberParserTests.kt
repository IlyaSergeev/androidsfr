package com.densvr.nfcreader

import org.junit.Test
import kotlin.test.assertEquals

class SFRChipNumberParserTests {

    @Test
    fun parseZeroArrayToPersonNumberTest() {

        assertEquals(0, "00000000".hexAsByteArray.readChipNumber(0))
    }
}