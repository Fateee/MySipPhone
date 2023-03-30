package com.sip.phone.net;

import android.util.Log
import com.common.network.base.NetworkApi
import com.common.network.errorhandler.ExceptionHandler
import com.common.network.model.MvvmNetworkObserver
import com.ec.encrypt.MD5Utils
import com.ec.utils.MMKVUtil
import com.sip.phone.constant.Constants
import com.sip.phone.sdk.SdkUtil
import com.sip.phone.util.NetUtil
import com.sip.phone.util.ToastUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Response
import java.net.ConnectException

class HttpPhone private constructor() : NetworkApi(){

    companion object {
        var SIGN_KEY = "828B739F7C7B2CC22948F35991F01CDC"
        private const val TAG = "HttpPhone_hy"
        const val STATISTICS_HOST = "http://fastapi.v6.army:8089/"
        val instance = Holder.holder
//        private val deviceId : String? = PushServiceFactory.getCloudPushService().deviceId

        fun <T> getService(service : Class<T>) : T {
            return instance.getRetrofit(service).create(service)
        }


        @JvmStatic
        fun authPhone(phone : String, needNetwork : Boolean = true, okCallback: (() -> Unit)? = null ) {
            val body: HashMap<String, String> = HashMap()
            val myPhone = MMKVUtil.decodeString(Constants.PHONE)
            val time = System.currentTimeMillis().toString()
            body["caller"] = myPhone?:""
            body["callee"] = phone
            body["timestamp"] = time
            val str = "caller=$myPhone&callee=$phone&timestamp=$time&key=$SIGN_KEY"
            val md5Str = MD5Utils.md5(str).uppercase()
            body["signature"] = md5Str
            val request = getService(PhoneApiList::class.java).authPhoneNum(body)
            val observer = SaObserver(null, object : MvvmNetworkObserver<Response<String>> {
                override fun onSuccess(data: Response<String>, isFromCache: Boolean) {
                    Log.i(TAG,"authPhone onSuccess")
                    val ret = data.body()
                    //{"msg":"认证成功","retCode":0,"signature":"A3CFC018141DC342E18B37DF346635B8","timestamp":1679997410}
                    if (!ret.isNullOrEmpty()) {
                        val json = JSONObject(ret)
                        if (json.optInt("retCode",-100) == 0) {
                            okCallback?.invoke()
                        } else {
                            ToastUtil.showToast(json.optString("msg"))
                        }
                    }
                }

                override fun onFailure(e: Throwable?) {
                    Log.i(TAG,"authPhone onFailure")
                }
            })
            if (needNetwork && !NetUtil.isNetworkConnected()) {
                observer.onError(ExceptionHandler.handleException(ConnectException("network error")))
                return
            }
            request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
        }

        @JvmStatic
        fun recordCallLog(needNetwork : Boolean = true) {
            if (SdkUtil.mDuration <= 0) return
            val body: HashMap<String, String> = HashMap()
            val myPhone = MMKVUtil.decodeString(Constants.PHONE)
            val phone = SdkUtil.mCallingPhone?:""
            body["caller"] = myPhone?:""
            body["callee"] = phone
            body["beginTime"] = SdkUtil.mBeginTime?:""
            body["duration"] = SdkUtil.mDuration.toString()
            val str = "caller=$myPhone&callee=$phone&beginTime=${SdkUtil.mBeginTime?:""}&duration=${SdkUtil.mDuration}&key=$SIGN_KEY"
            val md5Str = MD5Utils.md5(str).uppercase()
            body["signature"] = md5Str
            val request = getService(PhoneApiList::class.java).recordCallLog(body)
            val observer = SaObserver(null, object : MvvmNetworkObserver<Response<String>> {
                override fun onSuccess(data: Response<String>, isFromCache: Boolean) {
                    Log.i(TAG,"recordCallLog onSuccess")
                    val ret = data.body()
                    if (!ret.isNullOrEmpty()) {
                        val json = JSONObject(ret)
                        if (json.optInt("retCode",-100) == 0) {

                        } else {
                            ToastUtil.showToast(json.optString("msg"))
                        }
                    }
                }

                override fun onFailure(e: Throwable?) {
                    Log.i(TAG,"recordCallLog onFailure")
                }
            })
            if (needNetwork && !NetUtil.isNetworkConnected()) {
                observer.onError(ExceptionHandler.handleException(ConnectException("network error")))
                return
            }
            request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
        }
    }

    private object Holder {
        val holder = HttpPhone()
    }

    override fun <T : Any?> getAppErrorHandler(): Function<T, T> {
        return Function {response ->
            return@Function response
        }
    }

    override fun getInterceptor()= null

    override fun getTest() = STATISTICS_HOST

    override fun getOnline() = STATISTICS_HOST
}