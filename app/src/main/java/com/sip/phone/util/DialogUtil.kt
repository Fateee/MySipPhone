package com.sip.phone.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.sip.phone.R
import kotlinx.android.synthetic.main.dialog_title_msg_one_bts.view.*

object DialogUtil {

//    fun showPrivacyDialog(mContext: Context?, leftBtnListener: View.OnClickListener?, rightBtnListener: View.OnClickListener?, vararg msgGravity: Int): Dialog? {
//        if (mContext == null) return null
//        val root = LayoutInflater.from(mContext).inflate(R.layout.dialog_alert_privacy, null)
//        val msgView = root.findViewById<View>(R.id.message) as TextView
//        val cancelView = root.findViewById<View>(R.id.match_cancel) as TextView
//        val okView = root.findViewById<View>(R.id.match_ok) as TextView
//        //		msgView.setText(TextUtils.isEmpty(msg) ? "" : msg);
////		cancelView.setText(TextUtils.isEmpty(leftBtnStr) ? "取消" : leftBtnStr);
////		okView.setText(TextUtils.isEmpty(rightBtnStr) ? "确定" : rightBtnStr);
//        val text = SpannableStringBuilder(mContext.getString(R.string.privacy_dialog_content))
//        val firstIndex = mContext.getString(R.string.privacy_dialog_content).lastIndexOf("《壁纸用户协议》")
//        val firstLength = "《壁纸用户协议》".length
//        val secondIndex = mContext.getString(R.string.privacy_dialog_content).lastIndexOf("《隐私政策》")
//        val secondLength = "《隐私政策》".length
//        if (firstIndex > 1) {
//            val clickableSpan = PrivacyClickableSpan("壁纸用户协议",Consts.USER_URL)
//            text.setSpan(clickableSpan, firstIndex, firstIndex + firstLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        }
//        if (secondIndex > 1) {
//            val clickableSpan = PrivacyClickableSpan("隐私政策",Consts.PRIVACY_URL)
//            text.setSpan(clickableSpan, secondIndex, secondIndex + secondLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        }
//        msgView.text = text
//        msgView.movementMethod = LinkMovementMethod.getInstance()
//        msgView.highlightColor = Color.TRANSPARENT
//        val dialog: Dialog = AlertDialog.Builder(mContext, R.style.progress_dialog).create()
//        cancelView.setOnClickListener(leftBtnListener ?: View.OnClickListener { dialog.dismiss() })
//        okView.setOnClickListener { v: View? ->
//            dialog.dismiss()
//            rightBtnListener?.onClick(v)
//        }
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.show()
//        dialog.window?.setContentView(root)
//        val width = DensityUtil.getWidth()-DensityUtil.dip2px(90f)
//        val params = dialog.window?.attributes
//        params?.width = width
//        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window?.attributes = params
//        return dialog
//    }

    fun showTextDialog(mContext: Context?, title: String?, msg: String?, leftBtnListener: View.OnClickListener?, rightBtnListener: View.OnClickListener?, vararg msgGravity: Int): Dialog? {
        var gra = Gravity.START
        if (msgGravity.isNotEmpty()) {
            gra = msgGravity[0]
        }
        return showTextDialog(mContext, title, msg, "", "", leftBtnListener, rightBtnListener,"","", gra)
    }
    fun showTextDialog(mContext: Context?, title: String?, msg: String?, leftBtnStr: String?, rightBtnStr: String?, leftBtnListener: View.OnClickListener?, rightBtnListener: View.OnClickListener?, vararg msgGravity: Int): Dialog? {
        var gra = Gravity.START
        if (msgGravity.isNotEmpty()) {
            gra = msgGravity[0]
        }
        return showTextDialog(mContext, title, msg, leftBtnStr, rightBtnStr, leftBtnListener, rightBtnListener,"","", gra)
    }

    fun showTextDialog(mContext: Context?, title: String?, msg: String?, leftBtnStr: String?, rightBtnStr: String?, leftBtnListener: View.OnClickListener?, rightBtnListener: View.OnClickListener?,rightColor: String?, vararg msgGravity: Int): Dialog? {
        var gra = Gravity.START
        if (msgGravity.isNotEmpty()) {
            gra = msgGravity[0]
        }
        return showTextDialog(mContext, title, msg, leftBtnStr, rightBtnStr, leftBtnListener, rightBtnListener,"",rightColor, gra)
    }

    fun showTextDialog(mContext: Context?, title: String?, msg: String?, leftBtnStr: String?, rightBtnStr: String?, leftBtnListener: View.OnClickListener?, rightBtnListener: View.OnClickListener?,leftColor: String?,rightColor: String?, vararg msgGravity: Int): Dialog? {
        if (mContext == null) return null
        val root = LayoutInflater.from(mContext).inflate(R.layout.dialog_title_msg_two_bts, null)
        val dialogTitle = root.findViewById<View>(R.id.dialog_title) as TextView
        val msgView = root.findViewById<View>(R.id.message) as TextView
        val cancelView = root.findViewById<View>(R.id.match_cancel) as TextView
        val okView = root.findViewById<View>(R.id.match_ok) as TextView
        dialogTitle.text = if (TextUtils.isEmpty(title)) "" else title
        msgView.text = if (TextUtils.isEmpty(msg)) "" else msg
        if (!leftColor.isNullOrEmpty()) {
            cancelView.setTextColor(Color.parseColor(leftColor))
        }
        if (!rightColor.isNullOrEmpty()) {
            okView.setTextColor(Color.parseColor(rightColor))
        }
        if (!leftBtnStr.isNullOrEmpty()) {
            cancelView.text = leftBtnStr
        }
        if (!rightBtnStr.isNullOrEmpty()) {
            okView.text = rightBtnStr
        }
        if (msgGravity.isNotEmpty()) {
            msgView.gravity = msgGravity[0]
        }
        val dialog: Dialog = AlertDialog.Builder(mContext, R.style.progress_dialog).create()
        cancelView.setOnClickListener { v: View? ->
            dialog.dismiss()
            leftBtnListener?.onClick(v)
        }
        okView.setOnClickListener { v: View? ->
            dialog.dismiss()
            rightBtnListener?.onClick(v)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.window?.setContentView(root)
        val width = DensityUtil.getWidth()-DensityUtil.dip2px(80f)
        val params = dialog.window?.attributes
        params?.width = width
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = params
        return dialog
    }


    fun showOneBtDialog(mContext: Context?, title: String?="提示", msg: String?, msgColor: String?, btnStr: String?, btColor: String?, btnListener: View.OnClickListener?, vararg msgGravity: Int) : Dialog? {
        if (mContext == null) return null
        val root = LayoutInflater.from(mContext).inflate(R.layout.dialog_title_msg_one_bts, null)
        root.message.text = msg
        root.message.setCompoundDrawables(null,null,null,null)
        msgColor?.let {
            root.message.setTextColor(Color.parseColor(it))
        }
        btnStr?.let {
            root.match_ok.text = it
        }
        btColor?.let {
            root.match_ok.setTextColor(Color.parseColor(it))
        }
        title?.let {
            root.dialog_title.text = it
        }
        val dialog = AlertDialog.Builder(mContext, R.style.progress_dialog).create()
        root.match_ok.setOnClickListener { v: View? ->
            btnListener?.onClick(v)
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setContentView(root)
        val width = DensityUtil.getWidth()-DensityUtil.dip2px(106f)
        val params = dialog.window?.attributes
        params?.width = width
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = params
        return dialog
    }
//
//    fun showUseCouponDialog(mContext: Context?,okBtnListener: View.OnClickListener?) {
//        if (mContext == null) return
//        val root = LayoutInflater.from(mContext).inflate(R.layout.dialog_use_coupon, null)
//
//    }
}