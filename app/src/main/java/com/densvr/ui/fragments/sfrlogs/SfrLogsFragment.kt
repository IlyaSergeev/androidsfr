package com.densvr.ui.fragments.sfrlogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.densvr.androidsfr.R
import com.densvr.mock.nextSfrRecord
import com.densvr.nfc.SfrRecord
import com.densvr.nfc.toDelayTime
import com.densvr.ui.viewmodels.SfrRecordViewModel
import kotlinx.android.synthetic.main.fragment_sfr_logs.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class SfrLogsFragment : Fragment(R.layout.fragment_sfr_logs) {

    private val sfrRecordViewModel: SfrRecordViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sfrRecordViewModel.lastSfrRecord.observe(viewLifecycleOwner, Observer { sfrRecord ->
            updateRecords(sfrRecord, Date())
        })

        sfr_logs_test.setOnClickListener {
            sfrRecordViewModel.setRecord(Random.nextSfrRecord())
        }
    }

    private fun updateRecords(sfrRecord: SfrRecord?, date: Date) {

        if (sfrRecord != null) {

            val dateFormatter = SimpleDateFormat.getDateTimeInstance(
                SimpleDateFormat.SHORT,
                SimpleDateFormat.MEDIUM
            )

            sfr_logs_date_text.text = dateFormatter.format(date)
            sfr_logs_last_format_date_text.text = getString(
                R.string.sfr_logs_last_format_date,
                dateFormatter.format(sfrRecord.lastFormatTime)
            )
            val personIdString = "${sfrRecord.personNumber}"
            sfr_logs_person_number_text.text =
                getString(
                    R.string.sfr_logs_person_number, personIdString
                )

            val pointsCountString = "${sfrRecord.points.size}"
            sfr_logs_points_count.text = getString(
                R.string.sfr_logs_points_count, pointsCountString
            )

            val startPointIdString = "${sfrRecord.startPoint.pointId}"
            sfr_logs_start_point_id.text = getString(
                R.string.sfr_logs_start_point_id, startPointIdString
            )

            sfr_logs_chip_type.text = getString(
                R.string.sfr_logs_chip_type, sfrRecord.cipType?.name ?: "Unknown"
            )

            sfr_logs_points_text.text = sfrRecord.points.mapIndexed { index, sfrPointInfo ->
                "%03d: %03d->%s".format(
                    index + 1,
                    sfrPointInfo.pointId,
                    sfrPointInfo.timeMillis.toDelayTime()
                )
            }.joinToString("\n")
        }
    }
}