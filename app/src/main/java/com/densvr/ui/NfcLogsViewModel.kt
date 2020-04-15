package com.densvr.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.densvr.nfcreader.asNumeratedString
import java.text.SimpleDateFormat
import java.util.*

class NfcLogsViewModel : ViewModel() {

    private val mutableLastReadLogs = MutableLiveData<String>().apply {
        value = ""
    }

    val lastReadLogs: LiveData<String> = mutableLastReadLogs

    fun setLogs(bytes: ByteArray, operationDate: Date) {
        mutableLastReadLogs.value =
            SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM)
                .format(operationDate) + "\n\n" + bytes.asNumeratedString()
    }
}