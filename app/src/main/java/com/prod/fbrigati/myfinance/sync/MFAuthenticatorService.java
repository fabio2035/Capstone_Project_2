package com.prod.fbrigati.myfinance.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by FBrigati on 14/05/2017.
 */

public class MFAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MFAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MFAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
