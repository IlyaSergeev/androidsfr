package com.densvr.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.densvr.nfcreader.asNumeratedString

class NfcLogsViewModel : ViewModel() {

    private val mutableLastReadLogs = MutableLiveData<String>().apply {
        value = ""
    }

    val lastReadLogs: LiveData<String> = mutableLastReadLogs

    fun setLogs(bytes: ByteArray) {
        mutableLastReadLogs.value = bytes.asNumeratedString()
    }
}