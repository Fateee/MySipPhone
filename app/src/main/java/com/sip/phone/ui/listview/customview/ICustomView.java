package com.sip.phone.ui.listview.customview;

import android.os.Parcelable;

import java.io.Serializable;

public interface ICustomView<Y extends Serializable> {
    void setData(Y data);
    void setData(Y data, int pos,int size);
    void setStyle(int resId);
    void setActionListener(ICustomViewActionListener listener);
}
