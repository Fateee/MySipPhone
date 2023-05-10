package com.sip.phone.ui.setting

import com.ec.sdk.EcphoneSdk
import com.ec.utils.MMKVUtil
import com.sip.phone.R
import com.sip.phone.BuildConfig
import com.sip.phone.constant.Constants
import com.sip.phone.net.HttpPhone
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.base.BaseActivity
import com.sip.phone.update.UpdateDialog
import com.sip.phone.util.ToastUtil
import kotlinx.android.synthetic.main.activity_setting_list.*

class SettingActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_setting_list

    override fun initPages() {
        setlist_ring?.showSwitchButton(!SdkUtil.mNumSoundOff) { _, isOpen ->
            SdkUtil.mNumSoundOff = !isOpen
            MMKVUtil.encode(Constants.NUMBER_SOUND_OFF,SdkUtil.mNumSoundOff)
        }
        logout?.setOnClickListener {
            quitLogin()
        }
        appVersion?.text = "Version ${BuildConfig.VERSION_NAME}"
        checkVer?.setOnClickListener {
            checkAppVersion()
        }
    }

    private fun quitLogin() {
        SdkUtil.channelId = null
        SdkUtil.publicKey = null
        MMKVUtil.encode(Constants.PHONE,"")
        EcphoneSdk.unRegister()
        setResult(RESULT_OK)
        finish()
    }

    private fun checkAppVersion() {
        val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
        val account = MMKVUtil.decodeString(Constants.ENCRYPT_INFO_KEY) ?:""
        val location = MMKVUtil.decodeString(Constants.LOCATION_RESULT_KEY) ?:""
        HttpPhone.loginAndCheck(phoneCached,"",account,location) {
            when(it.code) {
                0,10 -> {
                    if (10 == it.code) {
                        UpdateDialog.show(this, it.data) {
                            quitLogin()
                        }
                    } else if (SdkUtil.channelId?.equals(it.data?.channelId) == false || SdkUtil.publicKey?.equals(it.data?.publicKey) == false) {
                        //如果使用中获取的配置数据与本地数据不同，则强制退出
                        ToastUtil.showToast("配置数据改变，退出登录")
                        quitLogin()
                    } else {
                        ToastUtil.showToast(it.message)
                    }
                }
            }
        }
    }
}