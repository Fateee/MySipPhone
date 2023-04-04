package com.sip.phone.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.common.network.base.NetworkApi
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

/**
 * Created by Admin on 2021/3/3.
 */
class DownloadUtil {
    private var okHttpClient: OkHttpClient? = null

    /**
     * 文件下载路径
     * android 10以后文件读写权限又更改，判断系统属于android10以上还是android10以下，更换路径
     */
    fun DownFullVideoPath(context: Context): String {
        val dirName: String
        //        String dirName = context.getExternalCacheDir().getAbsolutePath();
        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion >= 29) {
            dirName = context.externalCacheDir!!.path
            return dirName
        } else if (currentapiVersion < 29) {
            dirName = context.externalCacheDir!!.absolutePath
            return dirName
        }
        return ""
    }

    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称，后面记得拼接后缀，否则手机没法识别文件类型
     * @param listener     下载监听
     */
    fun download(url: String?, destFileDir: String?, destFileName: String?, listener: OnDownloadListener) {
        if (url.isNullOrEmpty()) return
        val request = Request.Builder().url(url).build()
//        val client = OkHttpClient()
//        try {
//            val response = client.newCall(request).execute()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        //异步请求
        okHttpClient?.newCall(request)?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 下载失败监听回调
                listener.onDownloadFailed(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                var `is`: InputStream? = null
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream? = null

                //储存下载文件的目录
                val dir = File(destFileDir)
                dir.delete()
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = File(dir, destFileName)
                Log.e("lz----:", " $file")
                try {
                    `is` = response.body?.byteStream()
                    val total = response.body?.contentLength() ?: -1
                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    while (`is`?.read(buf)?.also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        //下载中更新进度条
                        listener.onDownloading(progress)
                    }
                    fos.flush()
                    //下载完成
                    listener.onDownloadSuccess(file)
                } catch (e: Exception) {
                    listener.onDownloadFailed(e)
                } finally {
                    try {
                        `is`?.close()
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }
        })
    }

    interface OnDownloadListener {
        /**
         * 下载成功之后的文件
         */
        fun onDownloadSuccess(file: File?)

        /**
         * 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * 下载异常信息
         */
        fun onDownloadFailed(e: Exception?)
    }

    init {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.sslSocketFactory(NetworkApi.buildSslSocketFactory(), NetworkApi.TrustAllManager());
        okHttpClient = clientBuilder.build()
    }

    companion object {
        private var downloadUtil: DownloadUtil? = null
        fun get(): DownloadUtil? {
            if (downloadUtil == null) {
                downloadUtil = DownloadUtil()
            }
            return downloadUtil
        }
    }

}