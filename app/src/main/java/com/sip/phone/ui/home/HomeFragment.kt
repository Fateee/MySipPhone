package com.sip.phone.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sip.contact.PinyinComparator
import com.sip.contact.SimpleCornerTextView
import com.sip.contact.databinding.ItemFilterBinding
import com.sip.contact.sortlist.SortModel
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.constant.Constants
import com.sip.phone.database.HistoryBean
import com.sip.phone.database.HistoryManager
import com.sip.phone.net.HttpPhone
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.listview.customview.BaseCustomView
import com.sip.phone.ui.listview.recyclerview.BaseListAdapter
import com.sip.phone.ui.listview.recyclerview.OnItemClickCallback
import com.sip.phone.ui.setting.SettingActivity
import com.sip.phone.ui.view.DialView
import com.sip.phone.util.ContactUtil
import com.sip.phone.util.DateUtils
import com.sip.phone.util.TimeUtil
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.Serializable
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

//    private val PICK_CONTACT_REQUEST: Int = 666
//
//    private lateinit var homeViewModel: HomeViewModel
    private var mFilterListAdapter: FilterListAdapter? = null
    private var pinyinComparator = PinyinComparator()
    private var filterDateList = ArrayList<Serializable?>()
    private var searchKey: String? = ""
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        pickContack?.setOnClickListener {
//            // 创建 Intent，指定联系人选择器的操作
//            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
//
//            // 启动联系人选择器 Activity，并等待用户选择联系人
//            activity?.startActivityForResult(intent, PICK_CONTACT_REQUEST)
//        }
//        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialView?.setDialViewListener(object : DialView.DialViewListener {
            override fun inputChange() {
                filterData(dialView?.number)
            }

            override fun onCall(phone: String, name : String) {
                HttpPhone.authPhone(phone) {
                    SdkUtil.checkMyPermissions(context) {
                        SdkUtil.makeCall(phone, name)
                    }
                }
            }

            override fun onSetting() {}

            override fun onLongEvent(number: String?) {}
        })
        setting?.setOnClickListener {
            val intent = Intent(context,SettingActivity::class.java)
            activity?.startActivityForResult(intent, Constants.REQUEST_CANCEL_ACCOUNT)
        }
        filterRv?.layoutManager = LinearLayoutManager(context)
        mFilterListAdapter = FilterListAdapter()
        filterRv?.adapter = mFilterListAdapter
        mFilterListAdapter?.itemClickCallback = object : OnItemClickCallback {
            override fun onItemClick(itemView: View, data: Serializable, position: Int) {
                if (data is SortModel) {
                    dialView?.setDialViewInput(data.number)
//                    filterData(data.number)
                }
                if (data is HistoryBean) {
                    dialView?.setDialViewInput(data.phone)
                }
            }
        }
        val name = arguments?.getString("name")
        val number = arguments?.getString("number")
        if (!number.isNullOrEmpty()) {
            val arr = arrayOfNulls<String?>(2)
            arr[0] = name
            arr[1] = number
            setContactInfo(arr)
//            filterData(number)
        }
    }

    fun setContactInfo(ret: Array<String?>) {
        dialView?.setContactInfo(ret)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
//            // 获取所选联系人的 URI
//            val contactUri: Uri? = data?.data
//            if (contactUri == null) {
//                Toast.makeText(context,"contactUri == null",Toast.LENGTH_SHORT).show()
//                return
//            }
//            // 查询所选联系人的信息
//            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
//            val cursor: Cursor? = context?.contentResolver?.query(contactUri, projection, null, null, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                // 获取联系人的电话号码
//                val phoneNumber: String = cursor.getString(index)
//                // 在这里可以将所选联系人的相关信息返回给调用方应用程序
//                text_home?.text = phoneNumber
//
//            }
//            cursor?.close()
//        }
//    }

    private fun filterData(filterStr: String?) {
        searchKey = filterStr
        filterDateList.clear()
        if (!TextUtils.isEmpty(filterStr) && !SdkUtil.sourceDateList.isNullOrEmpty()) {
            for (sortModel in SdkUtil.sourceDateList!!) {
                val number = sortModel?.number
                if (number?.indexOf(filterStr!!) != -1) {
                    filterDateList.add(sortModel)
                }
            }
        }
        if (!TextUtils.isEmpty(filterStr) && !HistoryManager.mAllRecordList.isNullOrEmpty()) {
            for (item in HistoryManager.mAllRecordList) {
                val number = item.phone
                if (number.indexOf(filterStr!!) != -1) {
                    filterDateList.add(item)
                }
            }
        }
//        // 根据a-z进行排序
//        Collections.sort(filterDateList, pinyinComparator)
        mFilterListAdapter?.data = filterDateList as ArrayList<Serializable>
        mFilterListAdapter?.notifyDataSetChanged()
    }

    inner class FilterListAdapter : BaseListAdapter() {
        override fun createItemView(context: Context) = FilterItemView(context)
    }

    inner class FilterItemView(context: Context) : BaseCustomView<ItemFilterBinding, Serializable>(context) {
        override fun onRootClick(v: View?) {
        }

        override fun getViewLayoutId() = R.layout.item_record_view

        override fun setDataToView(data : Serializable?) {
            when(data) {
                is SortModel -> {
                    val name = data?.name
                    rootView?.findViewById<View>(R.id.contactGroup)?.visibility = View.VISIBLE
                    rootView?.findViewById<View>(R.id.recordGroup)?.visibility = View.GONE
                    val tvTitle = rootView?.findViewById<TextView>(R.id.title)
                    val tvPhone = rootView?.findViewById<TextView>(R.id.phone)
                    val icon = rootView?.findViewById<SimpleCornerTextView>(R.id.icon)
                    rootView?.findViewById<View>(R.id.catalog)?.visibility = View.GONE
                    if (!TextUtils.isEmpty(name)) {
                        tvTitle?.text = name
                        val lastWord = name?.substring(name.length - 1)
                        icon?.text = lastWord
                    }
                    var number : CharSequence? = data?.number
                    if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(searchKey)) {
                        if (number!!.contains(searchKey!!)) {
                            number = SpannableStringBuilder(number).apply {
                                val start = number!!.indexOf(searchKey!!)
                                val end = start + searchKey!!.length
                                setSpan(ForegroundColorSpan(context.resources.getColor(R.color.phone_main_color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                        tvPhone?.text = number
                        tvPhone?.visibility = View.VISIBLE
                    } else {
                        tvPhone?.visibility = View.GONE
                    }
                }
                is HistoryBean -> {
                    rootView?.findViewById<View>(R.id.contactGroup)?.visibility = View.GONE
                    rootView?.findViewById<View>(R.id.recordGroup)?.visibility = View.VISIBLE
                    val phoneName = rootView?.findViewById<TextView>(R.id.phoneName)
                    val recordTip = rootView?.findViewById<TextView>(R.id.recordTip)
                    val statusIcon = rootView?.findViewById<ImageView>(R.id.statusIcon)
                    val timeTv = rootView?.findViewById<TextView>(R.id.timeTv)
                    val tipBuilder = StringBuilder()
                    data.apply {
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
                            val ret = ContactUtil.getContentCallLog(MainApplication.app, phone)
                            phoneName?.text = ret?.displayName ?: phone
                        } else {
                            phoneName?.text = name
                        }
                        updateSearchWord(phoneName)

                        location?.let {
                            tipBuilder.append(" $it")
                        }
                        company?.let {
                            tipBuilder.append(" $it")
                        }
                        recordTip?.text = tipBuilder.toString()

                        val dateTime = TimeUtil.getFriendlyTimeByNow(date)
                        timeTv?.text = dateTime
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
}