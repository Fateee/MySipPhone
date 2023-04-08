package com.sip.phone.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.ec.utils.MMKVUtil
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.ThreadManager

object HistoryManager {
    private const val TAG = "HistoryManager_hy"
    private val MAX_ITEMS = 10000
    private val historyDao = HistoryDb.getInstance(MainApplication.app).historyDao()
    private var historyBean : HistoryBean? = null

//    init {
//        historyDao = HistoryDb.getInstance(MainApplication.app).historyDao()
//    }

    @WorkerThread
    private fun insert(bean : HistoryBean?) {
//        val ret = getAllHistory(bean.user_id)
//        if (ret?.size ?: 0 >= MAX_ITEMS) {
//            historyDao.deleteHistoryItem(ret?.get(0))
//        }
        historyDao.insertHistoryBean(bean)
    }

    private fun delete(bean : HistoryBean) {
        historyDao.deleteHistoryItem(bean)
    }

    fun getHistoryByPage(pageIndex: Int, userId: String?, type: Int=1) : List<HistoryBean>?{
        return historyDao.getHistoryByPage(pageIndex,userId,type)
    }

    fun deleteHistoryBean(deleteBeans : List<HistoryBean>?){
        return historyDao.deleteHistoryBean(deleteBeans)
    }

    fun getAllHistory(userId : String? = MMKVUtil.decodeString(Constants.PHONE)) : List<HistoryBean>?{
        return historyDao.getAllHistory(userId)
    }

    fun getAllHistoryByType(userId : String?, type: Int=1) : List<HistoryBean>?{
        return historyDao.getAllHistoryByType(userId,type)
    }

    fun clearMyAllData(userId : String?) {
        historyDao.deleteMyHistoryData(userId)
    }

    fun createRecord(thatPhone : String, name : String?, type : Int, location : String? = null, company : String? = null) {
        val myPhone = MMKVUtil.decodeString(Constants.PHONE) ?:""
        historyBean = HistoryBean(myPhone, thatPhone, name, type, 0, System.currentTimeMillis(), false, location, company)
    }

    fun updateLocation(location : String? = null) {
        historyBean?.location = location
    }

    fun saveRecord() {
        try {
            historyBean?.duration = SdkUtil.mDuration
            historyBean?.connect = SdkUtil.mDuration > 0
            ThreadManager.get().execute {
                insert(historyBean)
                clearTempRecord()
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun deleteRecord(bean : HistoryBean) {
        try {
            ThreadManager.get().execute {
                delete(bean)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun clearTempRecord() {
        historyBean = null
    }
}