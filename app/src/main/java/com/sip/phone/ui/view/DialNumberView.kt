package com.sip.phone.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ec.sdk.EcphoneSdk
import com.ec.utils.SipAudioManager
import com.sip.phone.R
import com.sip.phone.sdk.SdkUtil
import kotlinx.android.synthetic.main.dial_number_view.view.*

class DialNumberView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {


    init {
        View.inflate(context, R.layout.dial_number_view, this)

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
    }

    override fun onClick(v: View?) {
        if (v is ViewGroup) {
            val textView = v.getChildAt(0) as TextView
            val text = textView.text.toString()
            if (!SdkUtil.mNumSoundOff) {
                SipAudioManager.getInstance().playDTMF(text)
            }
            SdkUtil.callId?.let {
                EcphoneSdk.sendDtmf(it,text)
            }
        }
    }

}