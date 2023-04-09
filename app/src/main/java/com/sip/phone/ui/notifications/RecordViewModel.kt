package com.sip.phone.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sip.phone.database.HistoryBean
import com.sip.phone.database.HistoryManager

class RecordViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

//    val mAllRecordList = MutableLiveData<ArrayList<HistoryBean>>()

    val record : LiveData<HistoryBean?> = HistoryManager.newHistoryBean
}