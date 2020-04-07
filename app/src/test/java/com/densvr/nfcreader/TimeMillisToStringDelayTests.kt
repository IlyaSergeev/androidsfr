package com.densvr.nfcreader

import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class TimeMillisToStringDelayTests {

    @Test
    fun zeroMillisToDelayStringTest() {
        assertEquals("00", 0L.toDelayTime())
    }

    @Test
    fun zeroSecondWithMillisToDelayStringTest() {
        assertEquals("00.333", TimeUnit.MILLISECONDS.toMillis(333).toDelayTime())
    }

    @Test
    fun zeroSecondWithLess100MillisToDelayStringTest() {
        assertEquals("00.024", TimeUnit.MILLISECONDS.toMillis(24).toDelayTime())
    }

    @Test
    fun zeroSecondWith650MillisToDelayStringTest() {
        assertEquals("00.650", TimeUnit.MILLISECONDS.toMillis(650).toDelayTime())
    }

    @Test
    fun minutesWithSecondsToDelayStringTest() {
        assertEquals(
            "11:32",
            (TimeUnit.MINUTES.toMillis(11) + TimeUnit.SECONDS.toMillis(32)).toDelayTime()
        )
    }

    @Test
    fun minutesWith03SecondsToDelayStringTest() {
        assertEquals(
            "08:03",
            (TimeUnit.MINUTES.toMillis(8) + TimeUnit.SECONDS.toMillis(3)).toDelayTime()
        )
    }

    @Test
    fun minutesToDelayStringTest() {
        assertEquals("34:00", TimeUnit.MINUTES.toMillis(34).toDelayTime())
    }

    @Test
    fun minutesWith17SecondsAnd723MillisToDelayStringTest() {
        assertEquals(
            "04:17.723",
            (TimeUnit.MINUTES.toMillis(4) + TimeUnit.SECONDS.toMillis(17) + TimeUnit.MILLISECONDS.toMillis(
                723
            )).toDelayTime()
        )
    }

    @Test
    fun format23MinutesWith728MillisToDelayStringTest() {
        assertEquals(
            "23:00.728",
            (TimeUnit.MINUTES.toMillis(23) + TimeUnit.MILLISECONDS.toMillis(728)).toDelayTime()
        )
    }

    @Test
    fun hoursWithZeroMillisToDelayStringTest() {
        assertEquals("01:00:00", TimeUnit.HOURS.toMillis(1).toDelayTime())
    }

    @Test
    fun hoursWithMillisToDelayStringTest() {
        assertEquals("13:00:00.333", (TimeUnit.HOURS.toMillis(13) + TimeUnit.MILLISECONDS.toMillis(333)).toDelayTime())
    }

    @Test
    fun hoursWithLess100MillisToDelayStringTest() {
        assertEquals("04:00:00.024", (TimeUnit.HOURS.toMillis(4) + TimeUnit.MILLISECONDS.toMillis(24)).toDelayTime())
    }

    @Test
    fun hoursSecondWith650MillisToDelayStringTest() {
        assertEquals("67:00:00.650", (TimeUnit.HOURS.toMillis(67) + TimeUnit.MILLISECONDS.toMillis(650)).toDelayTime())
    }

    @Test
    fun hoursWithSecondsToDelayStringTest() {
        assertEquals(
            "22:11:32",
            (TimeUnit.HOURS.toMillis(22) + TimeUnit.MINUTES.toMillis(11) + TimeUnit.SECONDS.toMillis(32)).toDelayTime()
        )
    }

    @Test
    fun hoursWith03SecondsToDelayStringTest() {
        assertEquals(
            "43:08:03",
            (TimeUnit.HOURS.toMillis(43) + TimeUnit.MINUTES.toMillis(8) + TimeUnit.SECONDS.toMillis(3)).toDelayTime()
        )
    }

    @Test
    fun hoursToDelayStringTest() {
        assertEquals("07:34:00", (TimeUnit.HOURS.toMillis(7) + TimeUnit.MINUTES.toMillis(34)).toDelayTime())
    }

    @Test
    fun hoursWith17SecondsAnd723MillisToDelayStringTest() {
        assertEquals(
            "39:04:17.723",
            (TimeUnit.HOURS.toMillis(39) + TimeUnit.MINUTES.toMillis(4) + TimeUnit.SECONDS.toMillis(17) + TimeUnit.MILLISECONDS.toMillis(
                723
            )).toDelayTime()
        )
    }

    @Test
    fun formathoursWith23Minutes728MillisToDelayStringTest() {
        assertEquals(
            "44:23:00.728",
            (TimeUnit.HOURS.toMillis(44) + TimeUnit.MINUTES.toMillis(23) + TimeUnit.MILLISECONDS.toMillis(728)).toDelayTime()
        )
    }

}