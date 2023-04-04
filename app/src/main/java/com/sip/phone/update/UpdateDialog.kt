package com.sip.phone.update

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.sip.phone.R
import com.sip.phone.app.MainApplication
import com.sip.phone.bean.AppInfoBean
import com.sip.phone.util.*
import kotlinx.android.synthetic.main.dialog_alert_update.*
import java.io.File
import java.lang.Exception

class UpdateDialog : Dialog {
//    private var confirmListener: OnConfirmListener? = null

    constructor(context: Context) : super(context, R.style.progress_dialog) {
        init(context)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        init(context)
    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {
        init(context)
    }

    /**
     * 初始化
     *
     * @param context
     */
    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_alert_update, null)
        window?.setContentView(view)
        val width = DensityUtil.getWidth()- DensityUtil.dip2px(70f)
        val params = window?.attributes
        params?.width = width
        params?.height = DensityUtil.dip2px(358f)
        window?.attributes = params
    }

//    fun setConfirmListener(confirmListener: OnConfirmListener?) {
//        this.confirmListener = confirmListener
//    }
//
//    interface OnConfirmListener {
//        fun onConfirm(payType: String?)
//    }

    fun showDialog(data: AppInfoBean.DataBean?, dismissCallback: (() -> Unit)? = null) {
        if (data?.installFile.isNullOrEmpty() || data?.fileMd5.isNullOrEmpty()) return
        val isForce = true
        val fileDirPath = MainApplication.app.externalCacheDir?.absolutePath + File.separator + "CacheFiles"
        setCancelable(!isForce)
        setCanceledOnTouchOutside(!isForce)
        divider_vertical?.visibility = if (isForce) View.GONE else View.VISIBLE
        match_cancel?.visibility = if (isForce) View.GONE else View.VISIBLE
        message?.text = "1.优化用户体验\n2.修复已知问题"//data?.update_content
        match_ok?.setOnClickListener {
            match_ok?.visibility = View.GONE
            var progressValue = 0
            progressBarValue?.text = "$progressValue%"
            if (!isForce) {
                dismiss()
                ToastUtil.showToast("开始在后台下载....")
            } else {
                progressBarLayout?.visibility = View.VISIBLE
            }
            DownloadUtil.get()?.download(data?.installFile, fileDirPath, "app_newest.apk", object :DownloadUtil.OnDownloadListener{
                override fun onDownloadSuccess(file: File?) {
                    ThreadUtil.runOnMainThread {
                        val downloadFileMd5 = BinaryUtil.calculateMd5Str("$fileDirPath/app_newest.apk")
                        Log.i(TAG, "onDownloadSuccess and md5 $downloadFileMd5 ")
                        if (data?.fileMd5.equals(downloadFileMd5,true)) {
                            AppUtil.installApp(File("$fileDirPath/app_newest.apk"))
                        } else {
                            dismiss()
                            dismissCallback?.invoke()
                            ToastUtil.showToast("文件校验失败")
                        }
                    }
                }

                override fun onDownloading(progress: Int) {
                    ThreadUtil.runOnMainThread {
                        if (isForce) {
                            progressBarLayout?.post {
                                if (progress > progressValue) {
                                    progressValue = progress
                                    progressBar?.progress = progress
                                    progressBarValue?.text = "$progress%"
                                }
                            }
                        }
                    }
                }

                override fun onDownloadFailed(e: Exception?) {
                    ThreadUtil.runOnMainThread {
                        ToastUtil.showToast("下载失败")
                        FileUtils.deleteFile("$fileDirPath/app_newest.apk")
                    }
                }
            })

        }
        match_cancel?.setOnClickListener {
            dismiss()
        }

        show()
//        setOnDismissListener {
//            SAStatistics.track("app_basic","update_cancel")
//        }
    }
    companion object {
        private const val TAG = "UpdateDialog_hy"
        fun show(context: Context, data: AppInfoBean.DataBean?, dismissCallback: (() -> Unit)? = null): UpdateDialog {
            val dialog = UpdateDialog(context)
            dialog.showDialog(data,dismissCallback)
            return dialog
        }
    }
}