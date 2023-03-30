package com.sip.phone.ui.setting

import com.ec.sdk.EcphoneSdk
import com.ec.utils.MMKVUtil
import com.sip.phone.R
import com.sip.phone.constant.Constants
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_setting_list.*

class SettingActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_setting_list

    override fun initPages() {
        setlist_ring?.showSwitchButton(!SdkUtil.mNumSoundOff) { _, isOpen ->
            SdkUtil.mNumSoundOff = !isOpen
            MMKVUtil.encode(Constants.NUMBER_SOUND_OFF,SdkUtil.mNumSoundOff)
        }
        logout?.setOnClickListener {
            MMKVUtil.encode(Constants.PHONE,"")
            EcphoneSdk.unRegister()
            setResult(RESULT_OK)
            finish()
        }
    }
}