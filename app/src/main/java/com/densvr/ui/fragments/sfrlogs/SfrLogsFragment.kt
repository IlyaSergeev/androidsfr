package com.densvr.ui.fragments.sfrlogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.densvr.androidsfr.R
import com.densvr.androidsfr.databinding.FragmentSfrLogsBinding
import com.densvr.mock.nextSfrRecord
import com.densvr.nfc.SfrRecord
import com.densvr.nfc.toDelayTime
import com.densvr.ui.viewmodels.SfrRecordViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class SfrLogsFragment : Fragment() {

    private val sfrRecordViewModel: SfrRecordViewModel by activityViewModels()

    private var _binding: FragmentSfrLogsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSfrLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sfrRecordViewModel.lastSfrRecord.observe(viewLifecycleOwner) { sfrRecord ->
            updateRecords(sfrRecord, Date())
        }

        binding.sfrLogsTest.setOnClickListener {
            sfrRecordViewModel.setRecord(Random.nextSfrRecord())
        }
    }

    private fun updateRecords(sfrRecord: SfrRecord?, date: Date) {

        if (sfrRecord != null) {

            val dateFormatter = SimpleDateFormat.getDateTimeInstance(
                SimpleDateFormat.SHORT,
                SimpleDateFormat.MEDIUM
            )

            binding.sfrLogsDateText.text = dateFormatter.format(date)
            binding.sfrLogsLastFormatDateText.text = getString(
                R.string.sfr_logs_last_format_date,
                dateFormatter.format(sfrRecord.lastFormatTime)
            )
            val personIdString = "${sfrRecord.personNumber}"
            binding.sfrLogsPersonNumberText.text =
                getString(
                    R.string.sfr_logs_person_number, personIdString
                )

            val pointsCountString = "${sfrRecord.points.size}"
            binding.sfrLogsPointsCount.text = getString(
                R.string.sfr_logs_points_count, pointsCountString
            )

            val startPointIdString = "${sfrRecord.startPoint.pointId}"
            binding.sfrLogsStartPointId.text = getString(
                R.string.sfr_logs_start_point_id, startPointIdString
            )

            binding.sfrLogsChipType.text = getString(
                R.string.sfr_logs_chip_type, sfrRecord.cipType?.name ?: "Unknown"
            )

            binding.sfrLogsPointsText.text = sfrRecord.points.mapIndexed { index, sfrPointInfo ->
                "%03d: %03d->%s".format(
                    index + 1,
                    sfrPointInfo.pointId,
                    sfrPointInfo.timeMillis.toDelayTime()
                )
            }.joinToString("\n")
        }
    }
}