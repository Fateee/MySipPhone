package com.sip.phone.ui.listview.recyclerview;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.sip.phone.ui.listview.customview.ICustomView;

import java.io.Serializable;


public class BaseViewHolder extends RecyclerView.ViewHolder {
    ICustomView view;

    public BaseViewHolder(ICustomView itemView) {
        super((View) itemView);
        view = itemView;
    }

    public void bind(Serializable item) {
        view.setData(item);
    }

    public void bind(Serializable item, int position,int size) {
        view.setData(item,position,size);
    }
}
