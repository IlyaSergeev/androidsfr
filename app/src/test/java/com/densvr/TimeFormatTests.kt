package com.densvr

import com.densvr.util.secondsFormatString
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by i-sergeev on 4/3/21.
 */
class TimeFormatTests {
    @Test
    fun formatZeroSecondsFormat() {
        assertEquals("00", 0L.secondsFormatString())
    }

    @Test
    fun formatOneSecondsFormat() {
        assertEquals("01", 1L.secondsFormatString())
    }

    @Test
    fun formatTenSecondsFormat() {
        assertEquals("10", 10L.secondsFormatString())
    }

    @Test
    fun formatMinuteFormat() {
        assertEquals("01:00", 60L.secondsFormatString())
    }

    @Test
    fun formatTenMinuteFormat() {
        assertEquals("10:00", 600L.secondsFormatString())
    }

    @Test
    fun formatMinuteWithOneSecondFormat() {
        assertEquals("01:01", 61L.secondsFormatString())
    }

    @Test
    fun formatTenMinuteWithOneSecondFormat() {
        assertEquals("10:01", 601L.secondsFormatString())
    }

    @Test
    fun formatMinuteWithTenSecondFormat() {
        assertEquals("01:10", 70L.secondsFormatString())
    }

    @Test
    fun formatTenMinuteWithTenSecondFormat() {
        assertEquals("10:10", 610L.secondsFormatString())
    }

    @Test
    fun formatHourFormat() {
        assertEquals("01:00:00", 3600L.secondsFormatString())
    }

    @Test
    fun formatHourWithHourFormat() {
        assertEquals("01:01:00", 3660L.secondsFormatString())
    }

    @Test
    fun formatHourWithTenHourFormat() {
        assertEquals("01:10:00", 4200L.secondsFormatString())
    }

    @Test
    fun formatHourWithHourWithSecondFormat() {
        assertEquals("01:01:01", 3661L.secondsFormatString())
    }
}