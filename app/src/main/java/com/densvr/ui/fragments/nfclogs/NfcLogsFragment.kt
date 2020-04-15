package com.densvr.ui.fragments.nfclogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.densvr.androidsfr.R
import com.densvr.generator.nextSfrRecordBytes
import com.densvr.ui.NfcLogsViewModel
import kotlinx.android.synthetic.main.fragment_nfc_logs.*
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
            nfcLogsViewModel.setLogs(Random.nextSfrRecordBytes(), Date())
        }
    }
}