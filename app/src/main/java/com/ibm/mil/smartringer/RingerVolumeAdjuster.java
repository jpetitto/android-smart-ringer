package com.ibm.mil.smartringer;

import android.media.AudioManager;
import android.util.Log;

public final class RingerVolumeAdjuster {
    private static final String TAG = RingerVolumeAdjuster.class.getName();
    private static final int VOLUME_FLAGS = 0;

    private AudioManager mAudioManager;
    private final int mOriginalVolume;
    private final int mMaxVolume;
    private final int mAmpInterval;

    public RingerVolumeAdjuster(AudioManager audioManager) {
        mAudioManager = audioManager;
        mOriginalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        mAmpInterval = NoiseMeter.METER_LIMIT / mMaxVolume;
    }

    public void adjustVolume(int amplitude, SensitivityLevel sensitivityLevel) {
        int adjustedVolumeLevel = Math.max(1, amplitude / mAmpInterval);

        if (sensitivityLevel == SensitivityLevel.LOW) {
            adjustedVolumeLevel = Math.max(1, adjustedVolumeLevel - 1);
        } else if (sensitivityLevel == SensitivityLevel.HIGH) {
            adjustedVolumeLevel = Math.min(mMaxVolume, adjustedVolumeLevel + 1);
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, adjustedVolumeLevel, VOLUME_FLAGS);

        Log.d(TAG, "amplitude: " + amplitude);
        Log.d(TAG, "volume level: " + adjustedVolumeLevel);
    }

    public void restoreVolume() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mOriginalVolume, VOLUME_FLAGS);
    }

}
