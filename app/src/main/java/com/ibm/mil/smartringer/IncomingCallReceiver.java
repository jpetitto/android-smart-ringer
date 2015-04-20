package com.ibm.mil.smartringer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallReceiver extends BroadcastReceiver {
    private static final String TAG = IncomingCallReceiver.class.getName();

    private static PhoneStateListener phoneStateListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received system event for change in phone state");

        // register phone state listener only once
        if (phoneStateListener == null) {
            phoneStateListener = new IncomingCallPhoneStateListener(context);
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private static class IncomingCallPhoneStateListener extends PhoneStateListener {
        Context mContext;
        boolean isInitialRing = true;

        IncomingCallPhoneStateListener(Context context) {
            mContext = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING && isInitialRing) {
                mContext.startService(new Intent(mContext, RingerAdjusterService.class));

                // prevents CALL_STATE_RINGING from being triggered multiple times
                isInitialRing = false;
            }
        }
    }

}
