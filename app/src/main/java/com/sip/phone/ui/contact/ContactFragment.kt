package com.sip.phone.ui.contact

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.sip.contact.ClearEditText
import com.sip.contact.ConstactUtil
import com.sip.contact.PinyinComparator
import com.sip.contact.SortAdapter
import com.sip.contact.sortlist.CharacterParser
import com.sip.contact.sortlist.SideBar
import com.sip.contact.sortlist.SortModel
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.ToastUtil
import java.util.*
import kotlin.collections.ArrayList

class ContactFragment : Fragment() {

    private var root: View? = null
//    private lateinit var dashboardViewModel: DashboardViewModel
    private var sortListView: ListView? = null
    private var sideBar: SideBar? = null
    private var dialog: TextView? = null
    private var adapter: SortAdapter? = null
    private var mClearEditText: ClearEditText? = null

    private var characterParser: CharacterParser? = null
//    private var sourceDateList: ArrayList<SortModel?>? = null

    private var pinyinComparator: PinyinComparator? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        root = inflater.inflate(R.layout.activity_main_contact, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        sideBar = root?.findViewById<View>(R.id.sideBar) as SideBar
        dialog = root?.findViewById<View>(R.id.dialog) as TextView
        sortListView = root?.findViewById<View>(R.id.sortListView) as ListView
    }

    private fun initData() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance()
        pinyinComparator = PinyinComparator()
        sideBar?.setTextView(dialog)

        // 设置右侧触摸监听
        sideBar?.setOnTouchingLetterChangedListener { s -> // 该字母首次出现的位置
            val position = adapter?.getPositionForSection(s[0].toInt()) ?:0
            if (position != -1) {
                sortListView!!.setSelection(position)
            }
        }
        sortListView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                // Toast.makeText(getApplication(),
                // ((SortModel)adapter.getItem(position)).getName(),
                // Toast.LENGTH_SHORT).show();
                hideKeyboard()
                val name = (adapter?.getItem(position) as SortModel).name
                val number = SdkUtil.callRecords?.get(name)?.replace("-", "")?.replace(" ", "")
                val bundle = Bundle()
                bundle.putString("name", name)
                bundle.putString("number", number)
                findNavController(this).navigate(R.id.navigation_home, bundle)

            }
        if (SdkUtil.sourceDateList.isNullOrEmpty()) {
            SdkUtil.checkMyPermissions(context) {
                ContactAsyncTask().execute(0)
            }
        } else {
            initListView()
        }
    }

    inner class ContactAsyncTask : AsyncTask<Int?, Int?, Int>() {

        override fun doInBackground(vararg arg0: Int?): Int {
            var result = -1
            SdkUtil.callRecords = ConstactUtil.getAllCallRecords(MainApplication.app)
            result = 1
            return result
        }

        override fun onPostExecute(result: Int) {
            super.onPostExecute(result)
            if (result == 1) {
                val constact: MutableList<String> = ArrayList()
                val keys: Iterator<String>? = SdkUtil.callRecords?.keys?.iterator()
                while (keys?.hasNext() == true) {
                    val key = keys.next()
                    constact.add(key)
                }
                val names = constact.toTypedArray()
//                names = constact.toArray<String>(names)
//                names = constact.toTypedArray()
                SdkUtil.sourceDateList = filledData(names)

                // 根据a-z进行排序源数据
                Collections.sort(SdkUtil.sourceDateList, pinyinComparator)
                initListView()
            }
        }

    }

    private fun initListView() {
        adapter = SortAdapter(context, SdkUtil.sourceDateList)
        sortListView?.setAdapter(adapter)
        mClearEditText = root?.findViewById(R.id.filter_edit) as ClearEditText
        mClearEditText?.setOnFocusChangeListener(OnFocusChangeListener { arg0, arg1 ->
            mClearEditText?.setGravity(Gravity.LEFT or Gravity.CENTER_VERTICAL)
        })
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private fun filledData(date: Array<String>): ArrayList<SortModel?>? {
        val mSortList = ArrayList<SortModel?>()
        for (i in date.indices) {
            val sortModel = SortModel()
            sortModel.name = date[i]
            // 汉字转换成拼音
            val pinyin = characterParser?.getSelling(date[i])
            val sortString = pinyin?.substring(0, 1)?.toUpperCase()

            // 正则表达式，判断首字母是否是英文字母
            if (sortString?.matches(Regex("[A-Z]")) == true) {
                sortModel.sortLetters = sortString.toUpperCase()
            } else {
                sortModel.sortLetters = "#"
            }
            mSortList.add(sortModel)
        }
        return mSortList
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private fun filterData(filterStr: String) {
        var filterDateList = ArrayList<SortModel?>()
        if (TextUtils.isEmpty(filterStr) && !SdkUtil.sourceDateList.isNullOrEmpty()) {
            filterDateList = SdkUtil.sourceDateList!!
        } else {
            filterDateList.clear()
            for (sortModel in SdkUtil.sourceDateList!!) {
                val name = sortModel?.name
                if (name?.indexOf(filterStr) != -1 || characterParser?.getSelling(name)?.startsWith(filterStr) == true) {
                    filterDateList.add(sortModel)
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator)
        adapter?.updateListView(filterDateList)
    }

    fun hideKeyboard() {
        // 获取输入法管理器
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏键盘
        inputMethodManager.hideSoftInputFromWindow(mClearEditText?.windowToken, 0)

    }
}