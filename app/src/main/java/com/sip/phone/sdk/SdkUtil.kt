package com.sip.phone.sdk

import android.Manifest
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.easycalltech.ecsdk.EcSipLib
import com.easycalltech.ecsdk.business.location.LocationResult
import com.easycalltech.ecsdk.event.AccountRegisterEvent
import com.easycalltech.ecsdk.event.CallComingEvent
import com.ec.encrypt.Base64
import com.ec.encrypt.DataUtils
import com.ec.encrypt.RSAEncrypt
import com.ec.net.LocalConstant
import com.ec.net.entity.BaseResponse
import com.ec.sdk.EcphoneSdk
import com.ec.utils.MMKVUtil
import com.ec.utils.SipAudioManager
import com.sip.contact.sortlist.SortModel
import com.sip.phone.app.MainApplication
import com.sip.phone.call.outcall.OutCallFloatManager
import com.sip.phone.constant.Constants
import com.sip.phone.ui.MainActivity
import com.sip.phone.util.PermissionUtils
import com.sip.phone.util.ToastUtil

object SdkUtil {
    var mDuration: Long = 0
    var mBeginTime : String? = null
    var sourceDateList: ArrayList<SortModel?>? = null//联系人数据
    var callRecords: Map<String, String>? = null
    private const val TAG = "SdkUtil_hy"
    private var isOnceStartMainActivity = true;
    var doMain = "sc.vopbx.dict.cn"

    var isCallOuting = false; //是否正在呼出
    //是否静音
    private var isMicOff = false
    //是否扩音器
    private var isVolumeOpen = false
    //是否是主动拒听
    private var isDecline = false
    var mCallingPhone : String? = null
    var mCallingName : String? = null
    private var mCallComingEvent: CallComingEvent? = null
    /**
     * 接听
     */
    private var isAnswer = false;

    private fun getSignature(phone : String, channel : String = "cannelA", timestamp : Long = System.currentTimeMillis()): String? {
//        val timestamp = System.currentTimeMillis()
        val encrypt = RSAEncrypt.encrypt(
            RSAEncrypt.loadPublicKeyByStr(LocalConstant.ENCODE_PUBLIC_KEY),
            "{\"telephone\":\"$phone\",\"channel\":\"$channel\",\"timestamp\":${timestamp}}".toByteArray()
        )
        return Base64.encode(encrypt)
    }

    private fun init(
        signature: String,
        channel: String = "cannelA",
        timestamp: Long = System.currentTimeMillis(),
        okCallback: ((String?) -> Unit)?
    ) {
        EcphoneSdk.init(MainApplication.app, doMain, channel, "$timestamp", signature, object : EcphoneSdk.ResponseCallback<String> {
                override fun success(t: BaseResponse<String>) {
                    //todo hy save params
                    if (TextUtils.equals(t.retcode, "6")) {
//                        val intent:Intent = Intent(this@LoginActivity, BindActivity::class.java)
//                        intent.putExtra("phone",phone)
//                        startActivity(intent)
                    }
                    okCallback?.invoke(t.retcode)
                    ToastUtil.showDebug("ret code:${t.retcode} msg:${t.retmsg} ")
                }

                override fun fail(e: Throwable?) {
                    e?.printStackTrace()
                }
            })
    }

    /**
     * 1.先根据手机号init sdk
     * 2.init成功后再发送验证码
     * 3.bind成功后保存相关信息 以后不需要登录
     * 4.跳转至拨号界面
     **/
    fun initAndBindLoginFlow(phone: String, vcode: String="", loginBindAction : Boolean = false, callback: ((String?) -> Unit)? = null) {
        val timestamp = System.currentTimeMillis()
        val signature = getSignature(phone, timestamp = timestamp) ?: return ToastUtil.showToast("initAndBind signature为空")
        if (loginBindAction) {
            bind(phone,vcode) {
                initAndBindLoginFlow(phone,vcode,false, callback)
            }
        } else {
            init(signature, timestamp = timestamp) { code ->
                when(code) {
                    "0" -> {
                        //success
                        callback?.invoke(code)
                    }
                    "6" -> {
                        sendSmsCode(phone) {
                            callback?.invoke(code)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun sendSmsCode(phone: String, timerCallback: (() -> Unit)?) {
        val timestamp = System.currentTimeMillis()
        val encryptString = getSignature(phone, timestamp = timestamp) ?: return
        EcphoneSdk.sendRandomCode(
            "cannelA",
            "$timestamp",
            encryptString,
            DataUtils.getMD5DeviceId(MainApplication.app),
            object : EcphoneSdk.ResponseCallback<String> {
                override fun fail(e: Throwable?) {
                    ToastUtil.showToast(e?.message)
                }

                override fun success(t: BaseResponse<String>) {
                    ToastUtil.showToast(t.retmsg)
                    timerCallback?.invoke()
                }
            })
    }

    private fun bind(phone: String, vcode: String, okCallback: (() -> Unit)?) {
        val timestamp = System.currentTimeMillis()
        val encryptString = getSignature(phone, timestamp = timestamp) ?: return
        EcphoneSdk.bind(
            "cannelA",
            DataUtils.getMD5DeviceId(MainApplication.app),
            "$timestamp",
            encryptString,
            vcode,
            object : EcphoneSdk.ResponseCallback<String> {
                override fun fail(e: Throwable?) {
                    ToastUtil.showToast(e?.message)
                }

                override fun success(t: BaseResponse<String>) {
//                    ToastUtil.showToast(t.retmsg)
                    okCallback?.invoke()
                }
            }
        )
    }

    fun register(event: LocationResult) {
//        if (event.sipip == "-1") {
//            ToastUtil.showBottomShort("Location查询获取失败")
//        } else {
//            ToastUtil.showBottomShort("Location查询结果：$event")
//            Constants.SIP_HOST = event.sipip
//            Constants.SIP_PORT = event.sipPort
//            Constants.SIP_DOMAIN = event.doMain
//
////            uhost.setText(Constants.SIP_HOST)
////            uport.setText(Constants.SIP_PORT.toString() + "")
////            udomain.setText(Constants.SIP_DOMAIN)
//        }
//        val sipIp = "118.122.231.27"
//        val sipPort = 60000
//        EcphoneSdk.register(sipIp, sipPort, urlEditText.text.toString())

        if (event.sipip != "-1") {
            MMKVUtil.encode(Constants.HOST, event.sipip)
            MMKVUtil.encode(Constants.PORT, event.sipPort)
            MMKVUtil.encode(Constants.DOMAIN, event.doMain)
        }
        EcphoneSdk.register(event.sipip, event.sipPort, event.doMain)
        Log.i(TAG, "register sipip: ${EcphoneSdk.ecSdk?.mSIPIP} sipport: ${EcphoneSdk.ecSdk?.mSIPPort} domain: ${EcphoneSdk.ecSdk?.mDoMain}")
    }

    fun registerSuccess(event: AccountRegisterEvent, phone: String?, callback: (() -> Unit)? = null) {
        if (event.registrationStateCode == 200) {
            Log.i(TAG,"注册成功-----")
//            ToastUtil.showDebug("注册成功：")
            //   acquireWakeLock();
            // RegisterOKActivity.start(this, uname.getText().toString());
            if (isOnceStartMainActivity) {
//                val extNo = accountEditText.text.toString()
//                val extPwd = pwdEditText.text.toString()
                ///注册成功，保存账号密码
                val phoneCached = MMKVUtil.decodeString(Constants.PHONE)
                if (!phone.isNullOrEmpty() && !phone.equals(phoneCached)) {
                    MMKVUtil.encode(Constants.PHONE, phone) //保存手机号
                    MMKVUtil.encode(Constants.ACCOUNT, "")
                    MMKVUtil.encode(Constants.PWD, "")
                    //mine
                    MMKVUtil.encode(Constants.ACCOUNT_ID, event.accountID)
                    MMKVUtil.encode(Constants.REGISTER_STATE_CODE, event.registrationStateCode)

                    MainActivity.startActivity(event)
                    callback?.invoke()
                }
                isOnceStartMainActivity = false;
            }

        } else if (event.registrationStateCode == 408) {
            ToastUtil.showDebug("注册超时：")
            //  releaseWakeLock();
        } else {
            ToastUtil.showDebug("注册结果：代码:" + event.registrationStateCode)
            //   releaseWakeLock();
        }
    }

    fun answer(callComingEvent: CallComingEvent?) {
        mCallComingEvent = callComingEvent
        if (mCallComingEvent == null) return
        //停止振铃
        SipAudioManager.getInstance().stopRingtone()
//        callViewGone(imageInputCall)

        EcSipLib.getInstance(MainApplication.app).answerCall(mCallComingEvent!!.callID)

//        startVoiceTime = System.currentTimeMillis()
//        getInCall(v)
        isAnswer = true;
    }

    /**
     * 拒接
     */
    fun reject(callId :Int?, decline : Boolean) {
        //停止振铃
        SipAudioManager.getInstance().stopRingtone()
        isDecline = decline
        callId?.let { EcSipLib.getInstance(MainApplication.app).rejectCall(it) }
        resetParams()
    }

    /*** 呼出*****/

    fun makeCall(phone: String, name : String) {
        if (!isCallOuting) {
            makeACall(phone)
            OutCallFloatManager.instance.show(phone,name)
        }
    }

    private fun makeACall(phoneNumber: String) {
        SipAudioManager.getInstance().muteMicrophone(isMicOff)
        SipAudioManager.getInstance().setSpeakerMode(isVolumeOpen)
        EcSipLib.getInstance(MainApplication.app)?.makeCall(phoneNumber)
        isCallOuting = true;
    }

    //自己挂断
    fun hangup() {
        if (EcSipLib.getInstance(MainApplication.app)?.hangupAll() == 0) {
            Log.i(TAG, "endCall 电话挂断")
        }
    }

    fun switchSpeaker(): Boolean {
        isVolumeOpen = !isVolumeOpen
        SipAudioManager.getInstance().setSpeakerMode(isVolumeOpen)
        return isVolumeOpen
    }

    fun switchMute(): Boolean {
        isMicOff = !isMicOff
        SipAudioManager.getInstance().muteMicrophone(isMicOff)
        return isMicOff
    }

    private fun checkPhonePermissions(context: Context?, callback: PermissionUtils.Callback?) {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.REORDER_TASKS,
//            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE
        )
        PermissionUtils.checkPermission(context, callback, *permissions)
    }

    fun checkMyPermissions(context: Context?, okCallback: (() -> Unit)? = null) {
        checkPhonePermissions(context, object : PermissionUtils.Callback() {
            override fun onGranted() {
                super.onGranted()
                okCallback?.invoke()
            }
            override fun onDenied(context: Context) {
                super.onDenied(context)
                ToastUtil.showToast("拒绝权限申请可能导致功能无法正常使用！")
            }
        })
    }

    fun isRegistered(): Boolean {
        val sdk = EcphoneSdk.ecSdk ?: return false
        val field = sdk::class.java.getDeclaredField("mIsRegister")
        field.isAccessible = true
        val value = field.get(sdk)
        Log.e(TAG,"---------isPhoneRegistered---: $value")
        return value as Boolean
    }

    fun resetParams() {
        isAnswer = false
        mCallComingEvent = null
        isCallOuting = false
        isVolumeOpen = false
        mBeginTime = null
        mDuration = 0
        isMicOff = false
        mCallingPhone = null
        mCallingName  = null
    }
}