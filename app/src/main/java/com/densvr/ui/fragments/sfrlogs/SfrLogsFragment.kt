package com.densvr.ui.fragments.sfrlogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.densvr.androidsfr.R
import com.densvr.mock.nextSfrRecord
import com.densvr.ui.viewmodels.SfrRecordViewModel
import kotlinx.android.synthetic.main.fragment_sfr_logs.*
import kotlin.random.Random

class SfrLogsFragment : Fragment(R.layout.fragment_sfr_logs) {

    private val sfrRecordViewModel: SfrRecordViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sfrRecordViewModel.lastSfrRecord.observe(viewLifecycleOwner, Observer { lastLogs ->
            //TODO show sfr record
        })

        sfr_logs_test.setOnClickListener {
            sfrRecordViewModel.setLogs(Random.nextSfrRecord())
        }
    }
}