package com.ibm.mil.smartringer;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RingerAdjusterService extends IntentService {
    private static final String TAG = RingerAdjusterService.class.getName();

    public RingerAdjusterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service for adjusting ringer volume has been called");
        stopSelf();
    }

}
