package com.sip.phone.net;

import android.util.Log;

import com.common.network.errorhandler.ExceptionHandler;
import com.common.network.exception.ResponseThrowable;
import com.common.network.model.MvvmBaseModel;
import com.common.network.model.MvvmNetworkObserver;
import com.common.network.observer.BaseObserver;

import io.reactivex.annotations.NonNull;

public class SaObserver<T> extends BaseObserver<T> {
    private static final String TAG = "SaObserver";
    public SaObserver(MvvmBaseModel<T> baseModel, MvvmNetworkObserver<T> mvvmNetworkObserver) {
        super(baseModel, mvvmNetworkObserver);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        Log.e(TAG,"onError : "+e.getMessage());
        if (e instanceof ResponseThrowable) {
            mvvmNetworkObserver.onFailure(e);
        } else {
            mvvmNetworkObserver.onFailure(new ResponseThrowable(e, ExceptionHandler.ERROR.UNKNOWN));
        }
    }
}
