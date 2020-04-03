package com.densvr.nfcreader

import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class StringToMillisTests {

    @Test
    fun zeroHH_MM_SS_NeedBeZeroMillisTest() {

        val timeString = "00:00:00"
        assertEquals(0L, timeString.tryToTimeMillis())
    }

    @Test
    fun zeroMM_SS_NeedBeZeroMillisTest() {

        val timeString = "00:00"
        assertEquals(0L, timeString.tryToTimeMillis())
    }

    @Test
    fun zeroSS_NeedBeZeroMillisTest() {

        val timeString = "00"
        assertEquals(0L, timeString.tryToTimeMillis())
    }

    @Test
    fun tenSS_NeedBeTenSecondsTest() {

        val timeString = "10"
        assertEquals(TimeUnit.SECONDS.toMillis(10), timeString.tryToTimeMillis())
    }

    @Test
    fun moreThed60SecondsParserTest() {

        val timeString = "70"
        assertEquals(TimeUnit.SECONDS.toMillis(70), timeString.tryToTimeMillis())
    }
}