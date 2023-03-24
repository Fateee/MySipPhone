package com.sip.phone.sdk

import android.text.TextUtils
import com.ec.encrypt.Base64
import com.ec.encrypt.RSAEncrypt
import com.ec.net.LocalConstant
import com.ec.net.entity.BaseResponse
import com.ec.sdk.EcphoneSdk
import com.sip.phone.app.MainApplication

object SdkUtil {
    fun getSignature(phone : String, channel : String, timestamp : Long): String? {
//        val timestamp = System.currentTimeMillis()

        val encrypt = RSAEncrypt.encrypt(
            RSAEncrypt.loadPublicKeyByStr(
                LocalConstant.ENCODE_PUBLIC_KEY
            ),
            "{\"telephone\":\"$phone\",\"channel\":\"$channel\",\"timestamp\":${timestamp}}".toByteArray()
        )

        return Base64.encode(encrypt)
    }

    fun init(doMain : String, channel : String, timestamp : Long, signature : String) {

        EcphoneSdk.init(MainApplication.app, doMain, channel, "$timestamp", signature, object : EcphoneSdk.ResponseCallback<String> {
                override fun success(t: BaseResponse<String>) {
                    //todo hy save params
                    if (TextUtils.equals(t.retcode, "6")) {
//                        val intent:Intent = Intent(this@LoginActivity, BindActivity::class.java)
//
//                        intent.putExtra("phone",phone)
//
//                        startActivity(intent)
                    }
                }

                override fun fail(e: Throwable?) {

                }
            })
    }

}