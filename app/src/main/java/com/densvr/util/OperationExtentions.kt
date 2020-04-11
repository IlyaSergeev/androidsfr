package com.densvr.util

/**
 * Created by i-sergeev on 10.04.2020.
 */
inline fun <T> retryOnError(
    times: Int,
    errorIsValidCheck: (Throwable) -> Boolean,
    action: () -> T
): T {

    var lastError: Throwable? = null
    repeat(times) {
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
