package com.densvr.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NfcLogsViewModel : ViewModel() {

    private val mutableLastReadLogs = MutableLiveData<CharSequence>().apply {
        value = ""
    }

    val lastReadLogs: LiveData<CharSequence> = mutableLastReadLogs

    fun setLogs(log: CharSequence) {
        mutableLastReadLogs.value = log
    }
}