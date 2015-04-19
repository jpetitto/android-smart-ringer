package com.ibm.mil.smartringer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallReceiver extends BroadcastReceiver {
    private static final String TAG = IncomingCallReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String callState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Log.i(TAG, "Incoming phone call detected");
            context.startService(new Intent(context, RingerAdjusterService.class));
        }
    }

}
