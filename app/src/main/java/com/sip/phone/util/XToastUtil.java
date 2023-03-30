package com.sip.phone.util;

import android.app.Application;
import android.content.Context;
import android.view.View;

import com.hjq.xtoast.XToast;

public class XToastUtil {
	private static XToastUtil instance = null;

	private XToastUtil() {}

	public static XToastUtil getInstance() {
		if (instance == null) {
			instance = new XToastUtil();
		}
		return instance;
	}

	public void showToast(Application context, View view,int gravity, int px) {
		// 传入 Activity 对象表示设置成局部的，不需要有悬浮窗权限
		// 传入 Application 对象表示设置成全局的，但需要有悬浮窗权限
		new XToast<>(context)
				.setContentView(view)
				// 设置成可拖拽的
				//.setDraggable()
				// 设置显示时长
				.setDuration(1750)
				.setGravity(gravity)
				.setYOffset(px)
				// 设置动画样式
				//.setAnimStyle(android.R.style.Animation_Translucent)
				// 设置外层是否能被触摸
//				.setOutsideTouchable(true)
				// 设置窗口背景阴影强度
				//.setBackgroundDimAmount(0.5f)
//				.setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
//				.setText(android.R.id.message, "点我消失")
//				.setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {
//
//					@Override
//					public void onClick(XToast toast, TextView view) {
//						// 点击这个 View 后消失
//						toast.cancel();
//						// 跳转到某个Activity
//						// toast.startActivity(intent);
//					}
//				})
				.show();
	}
}
