package com.sip.phone.ui.notifications

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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

class RecordFragment : Fragment() {

    private var mRecordListAdapter: RecordListAdapter? = null
    private val mAllRecordList = ArrayList<HistoryBean>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                mAllRecordList.clear()
                mAllRecordList.addAll(it)
                mRecordListAdapter?.data = it as ArrayList<Serializable>
                mRecordListAdapter?.notifyDataSetChanged()
            }
        }
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
                        statusIcon?.setImageResource(R.drawable.ic_record_incall)
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
                        }
                    })
                } else {
                    phoneName?.text = name
                }

                location?.let {
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
    }
}