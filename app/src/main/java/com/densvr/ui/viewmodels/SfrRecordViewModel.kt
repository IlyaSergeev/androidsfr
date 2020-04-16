package com.densvr.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.densvr.nfc.SfrRecord

class SfrRecordViewModel : ViewModel() {

    private val mutableSfrRecord = MutableLiveData<SfrRecord?>().apply {
        value = null
    }

    val lastSfrRecord: LiveData<SfrRecord?> = mutableSfrRecord

    fun setRecord(srfRecord: SfrRecord?) {
        mutableSfrRecord.value = srfRecord
    }
}