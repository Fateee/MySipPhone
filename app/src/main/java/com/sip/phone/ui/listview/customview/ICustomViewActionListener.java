package com.sip.phone.ui.listview.customview;

import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

public interface ICustomViewActionListener {

    String ACTION_ROOT_VIEW_CLICKED = "action_root_view_clicked";

    void onAction(String action, View view, Serializable viewModel);
}
