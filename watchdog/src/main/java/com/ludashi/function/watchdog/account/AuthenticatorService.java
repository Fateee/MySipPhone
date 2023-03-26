package com.ludashi.function.watchdog.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author hly
 * @date 7/21/16
 */
public class AuthenticatorService extends Service {
    /** Instance field that stores the authenticator object */
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        super.onCreate();
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }
    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
