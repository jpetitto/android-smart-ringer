package com.ibm.mil.smartringer;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class RingerAdjusterService extends IntentService {
    private static final String TAG = RingerAdjusterService.class.getName();

    public RingerAdjusterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Service for adjusting ringer volume has been called");

        // initialize volume adjuster and mute ringer to perform proper noise level detection
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        VolumeAdjuster volumeAdjuster = new VolumeAdjuster(audioManager, AudioManager.STREAM_RING);
        volumeAdjuster.mute();

        // detect noise level for specified sensitivity and adjust ringer volume accordingly
        VolumeAdjuster.SensitivityLevel sensLevel = VolumeAdjuster.getUsersSensitivityLevel(this);
        NoiseMeter noiseMeter = new NoiseMeter();
        volumeAdjuster.adjustVolume(noiseMeter.getMaxAmplitude(), sensLevel);

        stopSelf();
    }

}
