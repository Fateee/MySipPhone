package com.sip.phone.ui.view

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.ec.utils.SipAudioManager
import com.sip.phone.R
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.MainActivity
import com.sip.phone.util.ContactUtil
import kotlinx.android.synthetic.main.dial_view.view.*

class DialView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    //    @BindViews({R.id.dialpad_0, R.id.dialpad_1, R.id.dialpad_2, R.id.dialpad_3, R.id.dialpad_4
    //            , R.id.dialpad_5, R.id.dialpad_6, R.id.dialpad_7, R.id.dialpad_8, R.id.dialpad_9
    //            , R.id.dialpad_star, R.id.dialpad_pound, R.id.dialpad_delete})
    //    List<View> mDialpadViews;
    //    @BindView(R.id.dialpad_delete)
    //    View mDialpadDelete;
    private var mDialpadInput: EditText? = null

    init {
        View.inflate(context, R.layout.dial_view, this)
        mDialpadInput = findViewById(R.id.dialpad_txt)
        mDialpadInput?.inputType = InputType.TYPE_NULL

        dialpad_0?.setOnClickListener(this)
        dialpad_1?.setOnClickListener(this)
        dialpad_2?.setOnClickListener(this)
        dialpad_3?.setOnClickListener(this)
        dialpad_4?.setOnClickListener(this)
        dialpad_5?.setOnClickListener(this)
        dialpad_6?.setOnClickListener(this)
        dialpad_7?.setOnClickListener(this)
        dialpad_8?.setOnClickListener(this)
        dialpad_9?.setOnClickListener(this)
        dialpad_star?.setOnClickListener(this)
        dialpad_pound?.setOnClickListener(this)
        remove_call?.setOnClickListener {
            onDelete(it)
        }
        fab_call?.setOnClickListener {
            onCall(it)
        }
        call_contact?.setOnClickListener {
            // 创建 Intent，指定联系人选择器的操作
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            // 启动联系人选择器 Activity，并等待用户选择联系人
            if (context is Activity) {
                startActivityForResult(context, intent, MainActivity.PICK_CONTACT_REQUEST,null)
            }
        }
        delete_num?.setOnClickListener {
            onDelete(it)
        }
        mDialpadInput?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()
                delete_num?.visibility = if (value.isNullOrEmpty()) {
                    View.GONE
                } else {
                    val ret = ContactUtil.getContentCallLog(context,value)
                    setContactName(ret?.displayName)
                    View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mDialpadInput?.setOnLongClickListener { view ->
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                view.showContextMenu()
            }
            true
        }

        mDialpadInput?.setOnCreateContextMenuListener { menu, view, menuInfo ->
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            menu.add(0, android.R.id.copy, 0, "复制").setOnMenuItemClickListener {
                try {
                    val clip = ClipData.newPlainText("text", mDialpadInput?.text.toString())
                    clipboard.setPrimaryClip(clip)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
                true
            }
            menu.add(0, android.R.id.paste, 0, "粘贴").setOnMenuItemClickListener {
                try {
                    val item = clipboard.primaryClip?.getItemAt(0)
                    if (item != null) {
                        mDialpadInput?.setText(item.text)
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
                true
            }
        }
    }

    interface DialViewListener {
        /**
         * 输入内容变化
         */
        fun inputChange()

        /**
         * 点击拨号按键
         */
        fun onCall(phone: String,name: String)

        /**
         * 点击设置按键
         */
        fun onSetting()

        /**
         * 长按事件
         */
        fun onLongEvent(number: String?)
    }

    private var mDialViewListener: DialViewListener? = null
    fun setDialViewListener(DialViewListener: DialViewListener?) {
        mDialViewListener = DialViewListener
    }

    /**
     * 获取输入框内容
     *
     * @return
     */
    var number: String?
        get() = mDialpadInput?.text?.toString()?:""
        set(number) {
            mDialpadInput?.setText(number)
        }

    /**
     * 清空输入框
     */
    fun cleanDialViewInput() {
        mDialpadInput?.setText("")
    }

    /**
     * 设置输入框内容
     *
     * @param input
     */
    fun setDialViewInput(input: String?) {
//        val pattern = Pattern.compile("[a-zA-z0-9]{1,15}")
//        if (pattern.matcher(input).matches()) {
//            mDialpadInput?.setText(input)
//        } else {
//            return
//        }
        mDialpadInput?.setText(input)
        if (mDialViewListener != null) {
            mDialViewListener!!.inputChange()
        }
    }

    fun onDialpad(v: View?) {
        val textView = when(v) {
            is ViewGroup -> v.getChildAt(0) as TextView
            is TextView -> v
            else -> return
        }
        if (!SdkUtil.mNumSoundOff) {
            SipAudioManager.getInstance().playDTMF(textView.text.toString())
        }
        mDialpadInput?.append(textView.text)
        if (mDialViewListener != null) {
            mDialViewListener!!.inputChange()
        }
    }

    fun onLongDialpad(viewGroup: ViewGroup): Boolean {
        val textView = viewGroup.getChildAt(0) as TextView
        if (mDialViewListener != null) {
            mDialViewListener!!.onLongEvent(textView.text.toString())
        }
        return true
    }

    fun onDelete(view: View?) {
        val content = mDialpadInput?.text.toString()
        if (!TextUtils.isEmpty(content)) {
            mDialpadInput?.setText(content.substring(0, content.length - 1))
        }
        if (mDialViewListener != null) {
            mDialViewListener!!.inputChange()
        }
    }

    fun onCall(view: View?) {
        val phone = mDialpadInput?.text?.toString()?.trim()?:""
        val name = tv_name?.text?.toString()?.trim()?:"未知"
        if (mDialViewListener != null && phone.isNotEmpty()) {
            mDialViewListener!!.onCall(phone,name)
        }
    }

    fun onSetting(view: View?) {
        if (mDialViewListener != null) {
            mDialViewListener!!.onSetting()
        }
    }

    override fun onClick(v: View?) {
        onDialpad(v)
    }

    fun setContactInfo(ret: Array<String?>) {
        val name = ret[0]
        val phone = ret[1]
        setContactName(name)
        setDialViewInput(phone)
    }

    private fun setContactName(name: String?) {
        if (name.isNullOrEmpty()) {
            tv_name?.text = ""
            tv_name?.visibility = View.INVISIBLE
        } else {
            tv_name?.text = name
            tv_name?.visibility = View.VISIBLE
        }
    }
}