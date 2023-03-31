package com.sip.phone.util

import android.os.AsyncTask
import com.sip.contact.ConstactUtil
import com.sip.contact.PinyinComparator
import com.sip.contact.sortlist.CharacterParser
import com.sip.contact.sortlist.SortModel
import com.sip.phone.app.MainApplication
import com.sip.phone.sdk.SdkUtil
import java.util.*
import kotlin.collections.ArrayList

class ContactAsyncTask(private val callBack : (()->Unit)? = null) : AsyncTask<Int?, Int?, Int>() {

    private var characterParser: CharacterParser? = CharacterParser.getInstance()
    private var pinyinComparator: PinyinComparator? = PinyinComparator()

    override fun doInBackground(vararg arg0: Int?): Int {
        var result = -1
        SdkUtil.callRecords = ConstactUtil.getAllCallRecords(MainApplication.app)
        result = 1
        return result
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        if (result == 1) {
//                val constact: MutableList<String> = ArrayList()
            SdkUtil.sourceDateList = ArrayList()
            SdkUtil.callRecords?.forEach { (k, v) ->
                val sortModel = SortModel()
                sortModel.name = k
                sortModel.number = v.replace("-", "")?.replace(" ", "")
                // 汉字转换成拼音
                val pinyin = characterParser?.getSelling(k)
                val sortString = pinyin?.substring(0, 1)?.toUpperCase()

                // 正则表达式，判断首字母是否是英文字母
                if (sortString?.matches(Regex("[A-Z]")) == true) {
                    sortModel.sortLetters = sortString.toUpperCase()
                } else {
                    sortModel.sortLetters = "#"
                }
                SdkUtil.sourceDateList?.add(sortModel)
            }

            // 根据a-z进行排序源数据
            Collections.sort(SdkUtil.sourceDateList, pinyinComparator)
            callBack?.invoke()
        }
    }
}