package com.densvr.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NfcLogsViewModel : ViewModel() {

    private val mutableLastReadLogs = MutableLiveData<String>().apply {
        value = ""
    }

    val lastReadLogs: LiveData<String> = mutableLastReadLogs

    fun setLogs(nfcLogs: String) {
        mutableLastReadLogs.value = nfcLogs
    }
}