package com.densvr.utility

import timber.log.Timber

/**
 * Created by i-sergeev on 10.04.2020.
 */
public inline fun <T> retryOnError(
    times: Int,
    errorIsValidCheck: (Throwable) -> Boolean,
    action: () -> T
): T {

    var lastError: Throwable? = null
    repeat(times) {
        Timber.tag("NFC").d("Try $it")
        try {
            return action()
        } catch (error: Throwable) {
            if (!errorIsValidCheck(error)) {
                throw error
            }
            lastError = error
        }
    }
    throw lastError!!
}
