package com.sip.phone.ui.listview.customview;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.io.Serializable;

public abstract class BaseCustomView<H extends ViewDataBinding,Y extends Serializable> extends LinearLayout implements ICustomView<Y>, View.OnClickListener {

    private H dataBinding;
    private Y viewModel;
    private ICustomViewActionListener mListener;

    @Override
    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        dataBinding.getRoot().setOnLongClickListener(onLongClickListener);
    }

    private OnLongClickListener onLongClickListener;
    public int position = -1;
    public int size = 0;

    public BaseCustomView(Context context) {
        super(context);
        init();
    }

    public BaseCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseCustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public View getRootView() {
        return dataBinding.getRoot();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutId = getViewLayoutId();
        if (layoutId != 0) {
            dataBinding = DataBindingUtil.inflate(inflater,layoutId,this,false);
            dataBinding.getRoot().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAction(ICustomViewActionListener.ACTION_ROOT_VIEW_CLICKED,v,viewModel);
                    }
                    onRootClick(v);
                }
            });
            addView(dataBinding.getRoot());
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        ViewGroup.LayoutParams contentParams = dataBinding.getRoot().getLayoutParams();
        params.width = contentParams.width;
        params.height = contentParams.height;
        super.setLayoutParams(params);
    }

    @Override
    public void setData(Y data) {
        viewModel = data;
        setDataToView(viewModel);
        if (dataBinding != null) {
            dataBinding.executePendingBindings();
        }
        onDataUpdated();
    }

    @Override
    public void setData(Y data, int pos,int size) {
        position = pos;
        this.size = size;
        setData(data);
    }

    protected void onDataUpdated() {

    }

    @Override
    public void setStyle(int resId) {

    }

    @Override
    public void setActionListener(ICustomViewActionListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {

    }

    public H getDataBinding() {
        return dataBinding;
    }

    public Y getViewModel() {
        return viewModel;
    }

    protected abstract void onRootClick(View v);

    protected abstract int getViewLayoutId();

    protected abstract void setDataToView(Y data);
}
