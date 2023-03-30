package com.sip.phone.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.sip.phone.R;


/**
 * Created by hy on 2018/2/7.
 */

public class SimpleToolbar extends Toolbar {
    /**
     * 左侧Title
     */
    private TextView mTxtLeftTitle;
    /**
     * 中间Title
     */
    private TextView mTxtMiddleTitle;
    /**
     * 右侧Title
     */
    private TextView mTxtRightTitle;

    public SimpleToolbar(Context context) {
        this(context,null);
    }

    public SimpleToolbar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.titleview, this);
        mTxtLeftTitle = (TextView) findViewById(R.id.txt_left_title);
        mTxtMiddleTitle = (TextView) findViewById(R.id.txt_main_title);
        mTxtRightTitle = (TextView) findViewById(R.id.txt_right_title);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.titleView, defStyleAttr, 0);

        int bgColor = a.getColor(R.styleable.titleView_titleBgColor, Color.WHITE);
        View titleView = rootView.findViewById(R.id.simple_toolbar);
        titleView.setBackgroundColor(bgColor);
        int lefticon = a.getResourceId(R.styleable.titleView_lefticon,0);
        if (lefticon != 0) {
            setLeftTitleDrawable(lefticon);
        }
        String lefttitle = a.getString(R.styleable.titleView_lefttitle);
        if (!TextUtils.isEmpty(lefttitle)) {
            setLeftTitleText(lefttitle);
        }

        String title = a.getString(R.styleable.titleView_toolbar_title);
        if (!TextUtils.isEmpty(title)) setMainTitle(title);
        int titleColor = a.getColor(R.styleable.titleView_titleColor, 0);
        if (titleColor != 0) {
            mTxtMiddleTitle.setTextColor(titleColor);
        }

        int righticon = a.getResourceId(R.styleable.titleView_righticon,0);
        if (righticon != 0) {
            setRightTitleDrawable(righticon);
        }
        String righttitle = a.getString(R.styleable.titleView_righttitle);
        if (!TextUtils.isEmpty(righttitle)) {
            setRightTitleText(righttitle);
        }
        boolean showDivider = a.getBoolean(R.styleable.titleView_showDivider,true);
        if (!showDivider) {
            rootView.findViewById(R.id.divider).setVisibility(View.GONE);
        }
        int rightTxtColor = a.getColor(R.styleable.titleView_rightTitleColor, Color.parseColor("#0C0C0C"));
        mTxtRightTitle.setTextColor(rightTxtColor);
        mTxtLeftTitle.setOnClickListener(view -> {
            if (getContext() instanceof Activity) {
                ((Activity)getContext()).finish();
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    //设置中间title的内容
    public void setMainTitle(String text) {
        this.setTitle("");
        mTxtMiddleTitle.setVisibility(View.VISIBLE);
        mTxtMiddleTitle.setText(text);
    }

    //设置中间title的内容文字的颜色
    public void setMainTitleColor(int color) {
        mTxtMiddleTitle.setTextColor(color);
    }

    //设置title左边文字
    public void setLeftTitleText(String text) {
        mTxtLeftTitle.setVisibility(View.VISIBLE);
        mTxtLeftTitle.setText(text);
    }

    //设置title左边文字颜色
    public void setLeftTitleColor(int color) {
        mTxtLeftTitle.setTextColor(color);
    }

    //设置title左边图标
    public void setLeftTitleDrawable(int res) {
        Drawable dwLeft = ContextCompat.getDrawable(getContext(), res);
        dwLeft.setBounds(0, 0, dwLeft.getMinimumWidth(), dwLeft.getMinimumHeight());
        mTxtLeftTitle.setCompoundDrawables(dwLeft, null, null, null);
    }
    //设置title左边点击事件
    public void setLeftTitleClickListener(OnClickListener onClickListener){
        mTxtLeftTitle.setOnClickListener(onClickListener);
    }

    //设置title右边文字
    public void setRightTitleText(String text) {
        mTxtRightTitle.setVisibility(View.VISIBLE);
        mTxtRightTitle.setText(text);
    }

    //设置title右边文字颜色
    public void setRightTitleColor(int color) {
        int colorValue = getResources().getColor(color);
        mTxtRightTitle.setTextColor(colorValue);
    }

    //设置title右边图标
    public void setRightTitleDrawable(int res) {
        Drawable dwRight = ContextCompat.getDrawable(getContext(), res);
        dwRight.setBounds(0, 0, dwRight.getMinimumWidth(), dwRight.getMinimumHeight());
        mTxtRightTitle.setCompoundDrawables(null, null, dwRight, null);
    }

    //设置title右边点击事件
    public void setRightTitleClickListener(OnClickListener onClickListener){
        mTxtRightTitle.setOnClickListener(onClickListener);
    }

}
