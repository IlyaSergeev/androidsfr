package com.densvr.nfcreader

import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class DelayStringToMillisTests {

    @Test
    fun zeroHH_MM_SS_NeedBeZeroMillisTest() {

        val timeString = "00:00:00"
        assertEquals(0L, timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun zeroMM_SS_NeedBeZeroMillisTest() {

        val timeString = "00:00"
        assertEquals(0L, timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun zeroSS_NeedBeZeroMillisTest() {

        val timeString = "00"
        assertEquals(0L, timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseMillisToMillisTest() {

        val timeString = "00.132"
        assertEquals(TimeUnit.MILLISECONDS.toMillis(132), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseSecondsWithMillisToMillisTest() {

        val timeString = "14.114"
        assertEquals(TimeUnit.SECONDS.toMillis(14) + TimeUnit.MILLISECONDS.toMillis(114), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseSecondsWithNanosecondsTest() {

        val timeString = "00.8973211"
        assertEquals(TimeUnit.MILLISECONDS.toMillis(897), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun tenSS_NeedBeTenSecondsTest() {

        val timeString = "10"
        assertEquals(TimeUnit.SECONDS.toMillis(10), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun moreThen60SecondsParserTest() {

        val timeString = "70"
        assertEquals(TimeUnit.SECONDS.toMillis(70), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parse10_13ToMillisTest() {
        val timeString = "10:13"
        assertEquals(TimeUnit.MINUTES.toMillis(10) + TimeUnit.SECONDS.toMillis(13), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parse16_04_40ToMillisTest() {
        val timeString = "16:04:40"
        assertEquals(TimeUnit.HOURS.toMillis(16) + TimeUnit.MINUTES.toMillis(4) + TimeUnit.SECONDS.toMillis(40), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseMinutesSecondsAndMillisToMillisTest() {
        val timeString = "32:13.444"
        assertEquals(TimeUnit.MINUTES.toMillis(32) + TimeUnit.SECONDS.toMillis(13) + TimeUnit.MILLISECONDS.toMillis(444), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseHoursMinutesSecondsAndMillisToMillisTest() {
        val timeString = "66:22:33.665"
        assertEquals(TimeUnit.HOURS.toMillis(66) + TimeUnit.MINUTES.toMillis(22) + TimeUnit.SECONDS.toMillis(33) + TimeUnit.MILLISECONDS.toMillis(665), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseIncorrectStringToMillisTest() {
        val timeString = "incerrectString"
        assertEquals(TimeUnit.MILLISECONDS.toMillis(0), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseIncorrectStringWithSecondsToMillisTest() {
        val timeString = "incerrectString:21"
        assertEquals(TimeUnit.SECONDS.toMillis(21), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseIncorrectStringSecondsMillisToMillisTest() {
        val timeString = "incerrectString:21"
        assertEquals(TimeUnit.SECONDS.toMillis(21), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseIncorrectStringWithMinutesAndSecondsToMillisTest() {
        val timeString = "incerrectString:01:33"
        assertEquals(TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(33), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseHoursIncorrectStringAndSecondsToMillisTest() {
        val timeString = "52:incerrectString:14"
        assertEquals(TimeUnit.HOURS.toMillis(52) + TimeUnit.SECONDS.toMillis(14), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseIncorrectStringWithMinutesSecondsMillisToMillisTest() {
        val timeString = "incerrectString:34:16.789"
        assertEquals(TimeUnit.MINUTES.toMillis(34) + TimeUnit.SECONDS.toMillis(16) + TimeUnit.MILLISECONDS.toMillis(789), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseHoursIncorrectStringSecondsMillisToMillisTest() {
        val timeString = "14:incerrectString:26.826"
        assertEquals(TimeUnit.HOURS.toMillis(14) + TimeUnit.SECONDS.toMillis(26) + TimeUnit.MILLISECONDS.toMillis(826), timeString.tryParseDelayMillisOrZero())
    }

    @Test
    fun parseIncorrectMillisStringToMillisTest() {
        val timeString = "incerrectString.322"
        assertEquals(TimeUnit.MILLISECONDS.toMillis(322), timeString.tryParseDelayMillisOrZero())
    }
}