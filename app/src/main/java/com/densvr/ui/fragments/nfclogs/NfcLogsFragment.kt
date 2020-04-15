package com.densvr.ui.fragments.nfclogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.densvr.androidsfr.R
import com.densvr.mock.nextSfrRecordBytes
import com.densvr.ui.NfcLogsViewModel
import com.densvr.util.SfrReaderLogger
import kotlinx.android.synthetic.main.fragment_nfc_logs.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NfcLogsFragment : Fragment(R.layout.fragment_nfc_logs) {

    private val nfcLogsViewModel: NfcLogsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcLogsViewModel.lastReadLogs.observe(viewLifecycleOwner, Observer { lastLogs ->
            nfc_logs_text.text = lastLogs
        })

        nfc_logs_test.setOnClickListener {
            val readLogger = SfrReaderLogger()
            readLogger.clear()
            readLogger.appendMessage(
                SimpleDateFormat.getDateTimeInstance(
                    SimpleDateFormat.SHORT,
                    SimpleDateFormat.MEDIUM
                ).format(Date())
            )
            readLogger.appendMessage("\n\n")

            val bytes = Random.nextSfrRecordBytes()
            readLogger.appendBytes(bytes, 0, bytes.size, 0)
            readLogger.appendError(IllegalStateException("Test exception"))

            nfcLogsViewModel.setLogs(readLogger.log)
        }
    }
}