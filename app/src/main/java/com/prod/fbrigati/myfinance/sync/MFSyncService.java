package com.prod.fbrigati.myfinance.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by FBrigati on 14/05/2017.
 */

public class MFSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MFSyncJob sMFSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MFSyncService", "onCreate - MFSyncJob");
        synchronized (sSyncAdapterLock) {
            if (sMFSyncAdapter == null) {
                sMFSyncAdapter = new MFSyncJob(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMFSyncAdapter.getSyncAdapterBinder();
    }
}
