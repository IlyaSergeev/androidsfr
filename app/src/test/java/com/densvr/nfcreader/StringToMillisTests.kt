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
    fun parseMillisToMillisTest() {

        val timeString = "00.132"
        assertEquals(TimeUnit.MILLISECONDS.toMillis(132), timeString.tryToTimeMillis())
    }

    @Test
    fun parseSecondsWithMillisToMillisTest() {

        val timeString = "14.114"
        assertEquals(TimeUnit.SECONDS.toMillis(14) + TimeUnit.MILLISECONDS.toMillis(114), timeString.tryToTimeMillis())
    }

    @Test
    fun parseSecondsWithNanosecondsTest() {

        val timeString = "00.8973211"
        assertEquals(TimeUnit.MILLISECONDS.toMillis(897), timeString.tryToTimeMillis())
    }

    @Test
    fun tenSS_NeedBeTenSecondsTest() {

        val timeString = "10"
        assertEquals(TimeUnit.SECONDS.toMillis(10), timeString.tryToTimeMillis())
    }

    @Test
    fun moreThen60SecondsParserTest() {

        val timeString = "70"
        assertEquals(TimeUnit.SECONDS.toMillis(70), timeString.tryToTimeMillis())
    }

    @Test
    fun parse10_13ToMillisTest() {
        val timeString = "10:13"
        assertEquals(TimeUnit.MINUTES.toMillis(10) + TimeUnit.SECONDS.toMillis(13), timeString.tryToTimeMillis())
    }

    @Test
    fun parse16_04_40ToMillisTest() {
        val timeString = "16:04:40"
        assertEquals(TimeUnit.HOURS.toMillis(16) + TimeUnit.MINUTES.toMillis(4) + TimeUnit.SECONDS.toMillis(40), timeString.tryToTimeMillis())
    }

    @Test
    fun parseMinutesSecondsAndMillisToMillis() {
        val timeString = "32:13.444"
        assertEquals(TimeUnit.MINUTES.toMillis(32) + TimeUnit.SECONDS.toMillis(13) + TimeUnit.MILLISECONDS.toMillis(444), timeString.tryToTimeMillis())
    }

    @Test
    fun parseHoursMinutesSecondsAndMillisToMillis() {
        val timeString = "66:22:33.665"
        assertEquals(TimeUnit.HOURS.toMillis(66) + TimeUnit.MINUTES.toMillis(22) + TimeUnit.SECONDS.toMillis(33) + TimeUnit.MILLISECONDS.toMillis(665), timeString.tryToTimeMillis())
    }
}