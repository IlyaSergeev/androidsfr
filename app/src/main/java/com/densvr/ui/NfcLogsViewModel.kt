package com.densvr.ui

import android.graphics.Color
import android.text.SpannableStringBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.densvr.nfcreader.asNumeratedString
import com.densvr.util.withColor
import java.text.SimpleDateFormat
import java.util.*

class NfcLogsViewModel : ViewModel() {

    private val mutableLastReadLogs = MutableLiveData<CharSequence>().apply {
        value = ""
    }

    val lastReadLogs: LiveData<CharSequence> = mutableLastReadLogs

    fun setLogs(bytes: ByteArray, error: Throwable?, operationDate: Date) {
        val stringBuilder = SpannableStringBuilder()
            .append(
                SimpleDateFormat.getDateTimeInstance(
                    SimpleDateFormat.SHORT,
                    SimpleDateFormat.MEDIUM
                ).format(operationDate)
            )
            .append("\n\n")
            .append(bytes.asNumeratedString())

        error?.let { storedError ->
            stringBuilder.append("\n\n")
                .append("!!!  ${error.message}".withColor(Color.RED))
        }

        mutableLastReadLogs.value = stringBuilder
    }
}