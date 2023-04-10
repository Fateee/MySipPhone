package com.sip.phone.ui.notifications

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants
import com.sip.phone.database.HistoryBean
import com.sip.phone.database.HistoryManager
import com.sip.phone.databinding.ItemRecordViewBinding
import com.sip.phone.ui.listview.customview.BaseCustomView
import com.sip.phone.ui.listview.recyclerview.BaseListAdapter
import com.sip.phone.ui.listview.recyclerview.OnItemClickCallback
import com.sip.phone.ui.listview.recyclerview.OnItemLongClickCallback
import com.sip.phone.util.ContactUtil
import com.sip.phone.util.DateUtils
import com.sip.phone.util.TimeUtil
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class RecordFragment : Fragment() {
    private val TAG = "RecordFragment_hy"
    private var mRecordListAdapter: RecordListAdapter? = null
    private var filterDataList = ArrayList<HistoryBean>()
    private var searchKey: String? = ""
    private lateinit var recordViewModel: RecordViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordRv?.layoutManager = LinearLayoutManager(context)
        mRecordListAdapter = RecordListAdapter()
        recordRv?.adapter = mRecordListAdapter
        mRecordListAdapter?.itemClickCallback = object : OnItemClickCallback {
            override fun onItemClick(itemView: View, data: Serializable, position: Int) {
                if (data is HistoryBean) {
                    if (!searchKey.isNullOrEmpty()) {
                        hideKeyboard()
                    }
                    val bundle = Bundle()
                    bundle.putString("name", data.name)
                    bundle.putString("number", data.phone)
                    NavHostFragment.findNavController(this@RecordFragment).navigate(R.id.navigation_home, bundle)
                }
            }
        }
        mRecordListAdapter?.itemLongClickCallback = object : OnItemLongClickCallback {
            override fun onItemLongClick(itemView: View?, data: Serializable, position: Int) {
                itemView?.showContextMenu()
            }
        }
        et_search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()
                filterData(value)
            }
        })
        if (HistoryManager.mAllRecordList.isNullOrEmpty()) {
            refreshList()
        } else {
            emptyTV?.visibility = View.GONE
//            mRecordListAdapter?.data?.clear()
            mRecordListAdapter?.data = HistoryManager.mAllRecordList as  ArrayList<Serializable>
            mRecordListAdapter?.notifyDataSetChanged()
        }
        recordViewModel.record.observe(viewLifecycleOwner, {
            mRecordListAdapter?.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    fun refreshList() {
        HistoryManager.getMyAllRecord {
            if (it.isNullOrEmpty()) {
                //暂无通话记录
                emptyTV?.visibility = View.VISIBLE
            } else {
                emptyTV?.visibility = View.GONE
//                mRecordListAdapter?.data?.clear()
                mRecordListAdapter?.data = HistoryManager.mAllRecordList as ArrayList<Serializable>
                mRecordListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun filterData(filterStr: String?) {
        searchKey = filterStr
        filterDataList.clear()
        if (filterStr.isNullOrEmpty()) {
            mRecordListAdapter?.data = HistoryManager.mAllRecordList as ArrayList<Serializable>
            mRecordListAdapter?.notifyDataSetChanged()
            return
        }
        if (!TextUtils.isEmpty(filterStr) && !HistoryManager.mAllRecordList.isNullOrEmpty()) {
            for (item in HistoryManager.mAllRecordList) {
                val number = item.phone
                val name = item.name
                if (number.indexOf(filterStr!!) != -1 || name?.indexOf(filterStr) != -1) {
                    filterDataList.add(item)
                }
            }
        }
        mRecordListAdapter?.data = filterDataList as ArrayList<Serializable>
        mRecordListAdapter?.notifyDataSetChanged()
    }

    inner class RecordListAdapter : BaseListAdapter() {
        override fun createItemView(context: Context) = RecordView(context)
    }

    inner class RecordView(context: Context) : BaseCustomView<ItemRecordViewBinding, HistoryBean>(context) {
        override fun onRootClick(v: View?) {
        }

        override fun getViewLayoutId() = R.layout.item_record_view

        override fun setDataToView(data: HistoryBean?) {
            val tipBuilder = StringBuilder()
            data?.apply {
                rootView?.findViewById<View>(R.id.recordGroup)?.visibility = View.VISIBLE
                val phoneName = rootView?.findViewById<TextView>(R.id.phoneName)
                val recordTip = rootView?.findViewById<TextView>(R.id.recordTip)
                val statusIcon = rootView?.findViewById<ImageView>(R.id.statusIcon)
                val timeTv = rootView?.findViewById<TextView>(R.id.timeTv)
                when(type) {
                    Constants.INCOME_CALL -> {
                        if (duration > 0) {
                            val ret = DateUtils.timeParseChar(duration * 1000)
                            tipBuilder.append("呼入$ret")
                        } else {
                            tipBuilder.append("未接通")
                        }
                        phoneName?.setTextColor(context.getColor(R.color.c0C0C0C))
                        statusIcon?.setImageDrawable(null)
                    }
                    Constants.INCOME_CALL_CANCEL -> {
                        val time = if (incall_show_time > 0) incall_show_time else 1
                        tipBuilder.append("响铃${time}秒")
                        phoneName?.setTextColor(context.getColor(R.color.cF53530))
                        statusIcon?.setImageResource(R.drawable.ic_record_duifang_quit)
                    }
                    Constants.INCOME_CALL_REJECT -> {
                        tipBuilder.append("拒接")
                        phoneName?.setTextColor(context.getColor(R.color.cF53530))
                        statusIcon?.setImageResource(R.drawable.ic_record_reject_call)
                    }
                    else -> {
                        if (duration > 0) {
                            val ret = DateUtils.timeParseChar(duration * 1000)
                            tipBuilder.append("呼出$ret")
                        } else {
                            tipBuilder.append("未接通")
                        }
                        phoneName?.setTextColor(context.getColor(R.color.black))
                        statusIcon?.setImageResource(R.drawable.ic_record_outcall)
                    }
                }

                if (name.isNullOrEmpty()) {
                    ContactUtil.getContentCallLog(MainApplication.app, phone, object : ContactUtil.Callback {
                        override fun onFinish(contentCallLog: ContactUtil.ContactInfo?) {
                            phoneName?.text = contentCallLog?.displayName ?: phone
                            updateSearchWord(phoneName)
                        }
                    })
                } else {
                    phoneName?.text = name
                    updateSearchWord(phoneName)
                }

                location?.let {
                    tipBuilder.append(" $it")
                }
                company?.let {
                    tipBuilder.append(" $it")
                }
                recordTip?.text = tipBuilder.toString()

                val dateTime = TimeUtil.getFriendlyTimeByNow(date)
                timeTv?.text = dateTime

                rootView?.setOnCreateContextMenuListener { menu, view, menuInfo ->
                    menu.add(0, android.R.id.copy, 0, "复制").setOnMenuItemClickListener {
                        try {
                            val clip = ClipData.newPlainText("text", phone)
                            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(clip)
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                        true
                    }
                    menu.add(0, R.id.delete, 0, "删除").setOnMenuItemClickListener {
                        try {
                            HistoryManager.deleteRecord(data) {
                                mRecordListAdapter?.data?.remove(data)
                                mRecordListAdapter?.notifyDataSetChanged()
                                if (mRecordListAdapter?.data.isNullOrEmpty()) {
                                    //暂无通话记录
                                    emptyTV?.visibility = View.VISIBLE
                                } else {
                                    emptyTV?.visibility = View.GONE
                                }
                            }
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                        true
                    }
                }
            }
        }

        private fun updateSearchWord(phoneName: TextView?) {
            var number : CharSequence? = phoneName?.text
            if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(searchKey)) {
                if (number!!.contains(searchKey!!)) {
                    number = SpannableStringBuilder(number).apply {
                        val start = number!!.indexOf(searchKey!!)
                        val end = start + searchKey!!.length
                        setSpan(ForegroundColorSpan(context.resources.getColor(R.color.phone_main_color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                phoneName?.text = number
            }
        }
    }

    fun hideKeyboard() {
        // 获取输入法管理器
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏键盘
        inputMethodManager.hideSoftInputFromWindow(et_search?.windowToken, 0)

    }
}