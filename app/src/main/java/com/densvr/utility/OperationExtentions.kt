package com.densvr.utility

/**
 * Created by i-sergeev on 10.04.2020.
 */
public inline fun <T> retryOnError(
    times: Int,
    action: () -> T,
    errorIsValidCheck: (Throwable) -> Boolean
): T {

    var lastError: Throwable? = null
    repeat(times) {
        try {
            action()
        } catch (error: Throwable) {
            if (!errorIsValidCheck(error)) {
                throw error
            }
            lastError = error
        }
    }
    throw lastError!!
}
