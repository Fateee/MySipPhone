package com.sip.phone.util

import android.content.Context
import android.os.Looper
import android.provider.ContactsContract

/**
 * Class  : ContactUtil
 */

object ContactUtil {
    /**¬
     * 根据电话号码获取联系人
     */
    fun getContentCallLog(mContext: Context?, number: String?): ContactInfo? {
        try {
            mContext?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    , arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    , ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                    , ContactsContract.CommonDataKinds.Phone.NUMBER)
                    , null
                    , null
                    , null
            )?.use { phoneCursor ->
                while (phoneCursor.moveToNext()) {
                    val columnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    if (columnIndex < 0) {
                        continue
                    }
                    var phoneNumber = phoneCursor.getString(columnIndex)
                    phoneNumber = phoneNumber?.replace("-", "")?.replace(" ", "")
                    if (number == phoneNumber) {
                        var displayName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        if (displayName == null) {
                            displayName = phoneNumber
                        }
                        return ContactInfo(displayName
                                , phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)))
                    }
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    data class ContactInfo(val displayName: String, val photoUri: String?)

    fun getContentCallLog(mContext: Context?, number: String?, callBack: Callback?) {
        ThreadManager.get().execute {
            val contentCallLog = getContentCallLog(mContext, number)
            callFinish(contentCallLog, callBack)
        }
    }

    private fun callFinish(log: ContactUtil.ContactInfo?, callBack: Callback?) {
        if (callBack == null) {
            return
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            callBack.onFinish(log)
            return
        }
        ThreadUtil.runOnMainThread { callBack.onFinish(log) }
    }

    interface Callback {
        fun onFinish(contentCallLog: ContactUtil.ContactInfo?)
    }
}