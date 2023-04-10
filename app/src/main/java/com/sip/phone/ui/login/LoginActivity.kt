package com.sip.phone.ui.login

import android.content.Intent
import android.os.CountDownTimer
import android.os.Process.killProcess
import android.os.Process.myPid
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.net.HttpPhone
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.ui.MainActivity
import com.sip.phone.ui.base.BaseActivity
import com.sip.phone.update.UpdateDialog
import com.sip.phone.util.OverlayUtil
import com.sip.phone.util.ToastUtil
import com.yushi.eventbustag.EventBusTag
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    companion object{
        private val TAG = "LoginActivity_hy"
        fun startActivity() {
            Log.i(TAG,"startActivity top_activity ${MainApplication.app.top_activity}")
            if (MainApplication.app.top_activity is MainActivity) {
                MainApplication.app.top_activity?.finish()
            }
            val intent = Intent(MainApplication.app, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MainApplication.app?.startActivity(intent)
        }
    }

    override fun getLayoutId() = R.layout.activity_login

    override fun initPages() {
        Log.i(TAG,"EventBusTag.isRegistered() ${EventBusTag.isRegistered()}")
//        if (!EventBusTag.isRegistered()) { //
//            EventBusTag.register(this)
//        }
//        val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
//        if (!phoneCached.isNullOrEmpty()) {
//            iv_login_logo?.visibility = View.GONE
//            tv_login_tip?.visibility = View.GONE
//            loginContainer?.visibility = View.GONE
//            //直接执行登录流程
//            SdkUtil.initAndBindLoginFlow(phoneCached)
//        } else {
//            iv_login_logo?.visibility = View.VISIBLE
//            tv_login_tip?.visibility = View.VISIBLE
//            loginContainer?.visibility = View.VISIBLE
//        }
        clearPhoneBt?.setOnClickListener {
            phoneNumEt?.setText("")
        }
        phoneNumEt?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val len = s?.toString()?.length ?:0
                clearPhoneBt?.visibility = if (len > 0) View.VISIBLE else View.GONE
            }
        })
        codeVerifyBt?.setOnClickListener {
            if (!isPhoneEmpty()) {
                HttpPhone.loginAndCheck(phoneNumEt?.text.toString().trim(), SdkUtil.generateRandomNumber().toString()) {
                    when(it.code) {
                        0,10 -> {
                            if (10 == it.code) {
                                UpdateDialog.show(this, it.data)
                            } else {
                                SdkUtil.updateAppInfo(it.data)
                                timer.start()
                            }
                        }
                        else -> ToastUtil.showToast(it.message)
                    }
                }
            }
        }
        loginBt?.setOnClickListener {
            if (!isPhoneEmpty() && !isCodeEmpty()) {
                val phone = phoneNumEt?.text.toString().trim()
                val vcode = codeVerifyEt?.text.toString().trim()
                if (!vcode.equals(SdkUtil.loginSmsCode.toString())) {
                    ToastUtil.showToast("验证码错误")
                } else {
                    SdkUtil.initAndBindLoginFlow(phone,"123456",true)
                }
            }
        }
        SdkUtil.checkMyPermissions(this,true)
        OverlayUtil.initOverlayPermission(this)
        OverlayUtil.autoSelfLaunchPermission(this)
    }

    private fun isPhoneEmpty() :Boolean {
        if (TextUtils.isEmpty(phoneNumEt?.text.toString().trim())) {
            ToastUtil.showToast("请输入手机号")
            return true
        }
        return false
    }

    private fun isCodeEmpty() :Boolean {
        if (TextUtils.isEmpty(codeVerifyEt?.text.toString().trim())) {
            ToastUtil.showToast("请输入验证码")
            return true
        }
        return false
    }

    var timer = object : CountDownTimer(60*1000,1000) {
        override fun onFinish() {
            codeVerifyBt?.isEnabled = true
            codeVerifyBt?.text = "重发验证码"
            SdkUtil.loginSmsCode = -1
        }

        override fun onTick(seconds: Long) {
            codeVerifyBt?.isEnabled = false
            codeVerifyBt?.text = (seconds/1000).toString()+"秒后重发"
        }

    }

    override fun isFullScreen() = true

    override fun isWhiteStatusBar() = false

//    @EventBusSub(tag = "AccountRegisterEvent")
//    fun accountRegistered(event: AccountRegisterEvent) {
//        Log.i(TAG, "### 账号注册消息 : " + ", AccountID: " + event.accountID + ", getRegistrationStateCode: " + event.registrationStateCode)
//        SdkUtil.registerSuccess(event,phoneNumEt?.text?.toString()?.trim()) {
//            finish()
//        }
//    }
//
//    /**
//     * 查询到location信息
//     *
//     * @param event
//     */
//    @EventBusSub(tag = "LocationResult")
//    fun locationResult(event: LocationResult) {
//        Log.i(TAG, "### locationResult $event")
//        SdkUtil.register(event)
//    }

    override fun onDestroy() {
        super.onDestroy()
//        ecSdk.destroy()
//        ecSdk.unRegister();
        timer.cancel()
//        EventBusTag.unregister(this);
    }

    override fun onBackPressed() {
        super.onBackPressed()
        killProcess(myPid())
    }
}