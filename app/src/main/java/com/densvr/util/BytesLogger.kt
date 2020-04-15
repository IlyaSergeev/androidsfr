package com.densvr.util

class BytesLogger {

    private val loggedBytes = arrayListOf<Byte>()

    val bytes: ByteArray
        get() = ByteArray(loggedBytes.size) { i -> loggedBytes[i] }

    var error: Throwable? = null
        private set

    fun clear() {
        loggedBytes.clear()
    }

    fun appendBytes(bytes: ByteArray, offset: Int) {
        appendBytes(bytes, offset, bytes.size - offset)
    }

    fun appendBytes(bytes: ByteArray, offset: Int, length: Int) {
        for (i in offset until offset + length) {
            loggedBytes += bytes[i]
        }
    }

    fun onErrorHappened(error: Throwable) {
        this.error = error
    }
}