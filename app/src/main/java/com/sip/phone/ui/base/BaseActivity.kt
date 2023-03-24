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
        setContentView(getLayoutId())
        setImmersiveBar(false)
        initPages()
    }
    abstract fun getLayoutId(): Int
    abstract fun initPages()
    /**
     * 设置侵入式bar
     */
    fun setImmersiveBar(isFullScreen: Boolean) {
        if (!isSetImmersiveBar) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                val decorView = window.decorView
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                var option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if (isFullScreen || isFullScreen) {
                    option = option or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isShowBlackFont) {
                    option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                decorView.systemUiVisibility = option
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = if (isWhiteStatusBar) Color.WHITE else Color.TRANSPARENT
            } else {
                val attributes = window.attributes
                val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                attributes.flags = attributes.flags or flagTranslucentStatus
                window.attributes = attributes
            }
        }
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
    val isSetImmersiveBar: Boolean
        get() = true

    /**
     * 是否是全屏
     * @return
     */
    val isFullScreen: Boolean
        get() = false

    /**
     * 是否显示黑色字体
     *
     * @return
     */
    val isShowBlackFont: Boolean
        get() = true

    /**
     * 是否显示百色状态栏
     *
     * @return
     */
    val isWhiteStatusBar: Boolean
        get() = true
}