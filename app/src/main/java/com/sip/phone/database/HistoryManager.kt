package com.sip.phone.database

import androidx.annotation.WorkerThread
import com.ec.utils.MMKVUtil
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.ThreadManager
import com.sip.phone.util.ThreadUtil

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

    private fun getHistoryByPage(pageIndex: Int, userId: String?, type: Int=1) : List<HistoryBean>?{
        return historyDao.getHistoryByPage(pageIndex,userId,type)
    }

    private fun deleteHistoryBean(deleteBeans : List<HistoryBean>?){
        return historyDao.deleteHistoryBean(deleteBeans)
    }

    private fun getAllHistory(userId : String? = MMKVUtil.decodeString(Constants.PHONE)) : List<HistoryBean>?{
        return historyDao.getAllHistory(userId)
    }

    private fun getAllHistoryByType(userId : String?, type: Int=1) : List<HistoryBean>?{
        return historyDao.getAllHistoryByType(userId,type)
    }

    fun deleteRecord(bean : HistoryBean, callback: (()->Unit)? = null) {
        try {
            ThreadManager.get().execute {
                delete(bean)
                ThreadUtil.runOnMainThread {
                    callback?.invoke()
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun clearMyAllData(userId : String?, callback: (()->Unit)? = null) {
        try {
            ThreadManager.get().execute {
                historyDao.deleteMyHistoryData(userId)
                callback?.invoke()
            }
        } catch (e : Exception) {
            e.printStackTrace()
            callback?.invoke()
        }
    }

    fun getMyAllRecord(callback: ((List<HistoryBean>?)->Unit)?) {
        try {
            ThreadUtil.runOnBackground({
                val ret = getAllHistory()
                ThreadUtil.runOnMainThread {
                    callback?.invoke(ret)
                }
            },true)
        } catch (e : Exception) {
            e.printStackTrace()
            callback?.invoke(null)
        }
    }

    fun createRecord(thatPhone : String, name : String?, type : Int, location : String? = null, company : String? = null) {
        val myPhone = MMKVUtil.decodeString(Constants.PHONE) ?:""
        historyBean = HistoryBean(myPhone, thatPhone, name, type, 0, System.currentTimeMillis(), false, location, company)
    }

    fun updateLocation(location : String? = null) {
        historyBean?.location = location
    }
    fun updateRecordType(type: Int) {
        historyBean?.type = type
    }

    fun saveRecord(callback: (()->Unit)? = null) {
        try {
            historyBean?.duration = SdkUtil.mDuration
            historyBean?.connect = SdkUtil.mDuration > 0
            historyBean?.incall_show_time = SdkUtil.mInCallShowTime
            if (SdkUtil.mDuration <= 0 && historyBean?.type == Constants.INCOME_CALL) {
                historyBean?.type = Constants.INCOME_CALL_CANCEL
            }
            ThreadManager.get().execute {
                insert(historyBean)
                clearTempRecord()
                callback?.invoke()
            }
        } catch (e : Exception) {
            e.printStackTrace()
            callback?.invoke()
        }
    }


    private fun clearTempRecord() {
        historyBean = null
    }
}