package com.sip.phone.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideActionBar()
        setImmersiveBar(false)
        if (!beforeSetContent()) return
        setContentView(getLayoutId())
        initPages()
    }

    open fun beforeSetContent(): Boolean {
        return true
    }
    abstract fun getLayoutId(): Int
    abstract fun initPages()
    /**
     * 设置侵入式bar
     */
    fun setImmersiveBar(isFullScreen: Boolean) {
        if (!isSetImmersiveBar()) return
        val window = window
        //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
        val decorView = window.decorView
        //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
        var option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (isFullScreen() || isFullScreen) {
            option = option or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isShowBlackFont()) {
            option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = if (isWhiteStatusBar()) Color.WHITE else Color.TRANSPARENT
        //        if (isFullScreen() || isFullScreen) {
//            setRootViewPadTop(getStatusBarHeight());
//        } else {
//            setRootViewPadTop(0);
//        }
    }

    /**
     * 是否显示侵入式状态栏
     *
     * @return
     */
    open fun isSetImmersiveBar(): Boolean {
        return true
    }

    /**
     * 是否是全屏
     * @return
     */
    open fun isFullScreen(): Boolean {
        return false
    }

    /**
     * 是否显示黑色字体
     *
     * @return
     */
    open fun isShowBlackFont(): Boolean {
        return true
    }

    /**
     * 是否显示百色状态栏
     *
     * @return
     */
    open fun isWhiteStatusBar(): Boolean {
        return true
    }

    private fun hideActionBar() {
        actionBar?.hide()
        supportActionBar?.hide()
    }
}