package com.sip.phone.net;

import android.util.Log
import com.alibaba.fastjson.JSON
import com.common.network.base.NetworkApi
import com.common.network.errorhandler.ExceptionHandler
import com.common.network.model.MvvmNetworkObserver
import com.ec.encrypt.DataUtils
import com.ec.encrypt.MD5Utils
import com.ec.utils.MMKVUtil
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.bean.AppInfoBean
import com.sip.phone.BuildConfig
import com.sip.phone.bean.LocateBean
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
        var SIGN_KEY = "828b739f7c7b2cc22948f35991f01cdc"
        private const val TAG = "HttpPhone_hy"
//        const val STATISTICS_HOST = "http://fastapi.v6.army:8089/"
        const val STATISTICS_HOST = "https://entry.v6.army:8089"
        val instance = Holder.holder
//        private val deviceId : String? = PushServiceFactory.getCloudPushService().deviceId

        fun <T> getService(service : Class<T>) : T {
            return instance.getRetrofit(service).create(service)
        }


        @JvmStatic
        fun authPhone(phone : String, needNetwork : Boolean = true, okCallback: ((Int,String?) -> Unit)? = null ) {
            val body: HashMap<String, String> = HashMap()
            val myPhone = MMKVUtil.decodeString(Constants.PHONE)
            val time = (System.currentTimeMillis()/1000).toString()
            body["caller"] = myPhone?:""
            body["callee"] = phone
            val uuid = DataUtils.getMD5DeviceId(MainApplication.app)
            body["uuid"] = uuid
            body["timestamp"] = time
            val str = "caller=$myPhone&callee=$phone&uuid=${uuid}&timestamp=$time&key=$SIGN_KEY"
            val md5Str = MD5Utils.md5(str).lowercase()
            body["signature"] = md5Str
            val request = getService(PhoneApiList::class.java).authPhoneNum(body)
            val observer = SaObserver(null, object : MvvmNetworkObserver<Response<String>> {
                override fun onSuccess(data: Response<String>, isFromCache: Boolean) {
                    val ret = data.body()
                    Log.i(TAG,"authPhone onSuccess $ret")
                    //{"msg":"认证成功","retCode":0,"signature":"A3CFC018141DC342E18B37DF346635B8","timestamp":1679997410}
                    if (!ret.isNullOrEmpty()) {
                        val json = JSONObject(ret)
                        val code = json.optInt("code",-100)
                        val msg = json.optString("message")
                        okCallback?.invoke(code,msg)
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
            val time = (System.currentTimeMillis()/1000).toString()
            body["caller"] = myPhone?:""
            body["callee"] = phone
            body["startTime"] = SdkUtil.mBeginTime?:""
            body["duration"] = SdkUtil.mDuration.toString()
            body["timestamp"] = time
            val str = "caller=$myPhone&callee=$phone&startTime=${SdkUtil.mBeginTime?:""}&duration=${SdkUtil.mDuration}&timestamp=$time&key=$SIGN_KEY"
            val md5Str = MD5Utils.md5(str).lowercase()
            body["signature"] = md5Str
            val request = getService(PhoneApiList::class.java).recordCallLog(body)
            val observer = SaObserver(null, object : MvvmNetworkObserver<Response<String>> {
                override fun onSuccess(data: Response<String>, isFromCache: Boolean) {
                    val ret = data.body()
                    Log.i(TAG,"recordCallLog onSuccess $ret")
                    if (!ret.isNullOrEmpty()) {
                        val json = JSONObject(ret)
                        if (json.optInt("code",-100) == 0) {

                        } else {
                            ToastUtil.showToast(json.optString("message"))
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

        @JvmStatic
        fun loginAndCheck(mobile : String?=null, password : String?=null, account : String?="",
                          location : String?="" ,callback: ((AppInfoBean) -> Unit)? = null) {
            val body: HashMap<String, String> = HashMap()
            val uuid = DataUtils.getMD5DeviceId(MainApplication.app)
            val time = (System.currentTimeMillis()/1000).toString()
            val appName = MainApplication.app.getString(R.string.app_name)
            val appVer = BuildConfig.VERSION_NAME
            var str = ""
            if (!mobile.isNullOrEmpty()) {
                body["mobile"] = mobile
                str += "mobile=${mobile}"
            }
            if (password != null) {
                body["password"] = password
                str += "&password=${password}"
            }
            body["uuid"] = uuid
            str += "&uuid=${uuid}"
            if (account != null) {
                body["account"] = account
                str += "&account=${account}"
            }
            if (location != null) {
                body["location"] = location
                str += "&location=${location}"
            }
            body["appName"] = appName
            str += "&appName=${appName}"
            body["appVer"] = appVer
            str += "&appVer=${appVer}"
            body["timestamp"] = time
            str += "&timestamp=${time}"

            str += "&key=$SIGN_KEY"
            val md5Str = MD5Utils.md5(str).lowercase()
            body["signature"] = md5Str
            Log.i(TAG,"loginAndCheck body: $body")
            Log.i(TAG,"loginAndCheck str: $str")
            val request = getService(PhoneApiList::class.java).loginAndCheck(body)
            val observer = SaObserver(null, object : MvvmNetworkObserver<Response<String>> {
                override fun onSuccess(data: Response<String>, isFromCache: Boolean) {
                    val ret = data.body()
                    Log.i(TAG,"loginAndCheck onSuccess $ret")
                    if (!ret.isNullOrEmpty()) {
                        val bean = JSON.parseObject(ret, AppInfoBean::class.java)
                        if (bean != null) {
//                            bean.code = 0
//                            bean.data = AppInfoBean.DataBean().apply {
//                                channelId = "cannelA"
//                                publicKey = LocalConstant.ENCODE_PUBLIC_KEY
//                                installFile="https://m.gdown.baidu.com/407bc0a567d7ed57730606a3adcd3de8a5adf237fe4dc3e5461c3c7ffa53517ce5538fd2dfcd2224b6cc03d940c08e920a1ad1968a64789e9d98e778b6252f6b3b74c4c9bfbb483043ad98bf8107784a8f4f3e2fa3ecb35ade2476d3bf91124b5fed388be4d6d2f7c6d2dd40bf8f83f5c17f44942826d8e6b4d94001c5a7a9e9780d344c22e274faa9a3039be87e87d55566aed99186bc3771ca4311d979fc632b98010f908bbc12539f4d82a0acc86cc90006c68175a86b"
//                                fileMd5="sdfds"
//                            }
                            callback?.invoke(bean)
                        }
                    }
                }

                override fun onFailure(e: Throwable?) {
                    Log.i(TAG,"loginAndCheck onFailure")
                }
            })
            if (!NetUtil.isNetworkConnected()) {
                observer.onError(ExceptionHandler.handleException(ConnectException("network error")))
                return
            }
            request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
        }

        @JvmStatic
        fun location(phone : String?=null, type : String?="out", callback: ((LocateBean.DataBean?) -> Unit)? = null) {
            val body: HashMap<String, String> = HashMap()
            val myPhone = MMKVUtil.decodeString(Constants.PHONE)
            val time = (System.currentTimeMillis()/1000).toString()
            var str = ""
            if (!myPhone.isNullOrEmpty()) {
                body["mobile"] = myPhone
                str += "mobile=${myPhone}"
            }
            if (!phone.isNullOrEmpty()) {
                body["phone"] = phone
                str += "&phone=${phone}"
            }
            if (!type.isNullOrEmpty()) {
                body["type"] = type
                str += "&type=${type}"
            }
            body["timestamp"] = time
            str += "&timestamp=${time}&key=$SIGN_KEY"
            val md5Str = MD5Utils.md5(str).lowercase()
            body["signature"] = md5Str
            Log.i(TAG,"location body: $body")
            Log.i(TAG,"location str: $str")
            val request = getService(PhoneApiList::class.java).location(body)
            val observer = SaObserver(null, object : MvvmNetworkObserver<Response<String>> {
                override fun onSuccess(data: Response<String>, isFromCache: Boolean) {
                    val ret = data.body()
                    Log.i(TAG,"location onSuccess $ret")
                    if (!ret.isNullOrEmpty()) {
                        val bean = JSON.parseObject(ret, LocateBean::class.java)
                        if (bean != null) {
                            if (bean.code == 0) {
                                callback?.invoke(bean.data)
                            } else {
                                ToastUtil.showToast(bean.message)
                            }
                        }
                    }
                }

                override fun onFailure(e: Throwable?) {
                    Log.i(TAG,"location onFailure")
                }
            })
            if (!NetUtil.isNetworkConnected()) {
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