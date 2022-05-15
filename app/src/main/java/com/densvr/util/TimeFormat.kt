package com.densvr.util

import java.util.concurrent.TimeUnit

/**
 * Created by i-sergeev on 4/3/21.
 */
fun Long.secondsFormatString() : String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val timeWithoutHours = this - TimeUnit.HOURS.toSeconds(hours)
    val minutes = TimeUnit.SECONDS.toMinutes(timeWithoutHours)
    val timeWithoutMinutes = timeWithoutHours - TimeUnit.MINUTES.toSeconds(minutes)
    val seconds = TimeUnit.SECONDS.toSeconds(timeWithoutMinutes)

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
    else if (minutes > 0) {
        "%02d:%02d".format(minutes, seconds)
    }
    else {
        "%02d".format(seconds)
    }
}