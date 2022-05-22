package com.densvr.ui.fragments.nfclogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.densvr.androidsfr.databinding.FragmentNfcLogsBinding
import com.densvr.mock.nextSfrRecordBytes
import com.densvr.ui.viewmodels.NfcLogsViewModel
import com.densvr.util.NfcReaderLogger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NfcLogsFragment : Fragment() {

    private val nfcLogsViewModel: NfcLogsViewModel by activityViewModels()
    private var _bindings : FragmentNfcLogsBinding? = null
    private val bindings get() = _bindings!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindings = FragmentNfcLogsBinding.inflate(inflater, container, false)
        return bindings.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcLogsViewModel.lastReadLogs.observe(viewLifecycleOwner) { lastLogs ->
            bindings.nfcLogsTest.text = lastLogs
        }

        bindings.nfcLogsTest.setOnClickListener {
            val readLogger = NfcReaderLogger()
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