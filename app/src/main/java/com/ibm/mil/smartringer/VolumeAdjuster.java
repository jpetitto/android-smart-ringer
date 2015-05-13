package com.ibm.mil.smartringer;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public final class VolumeAdjuster {
    private static final String TAG = VolumeAdjuster.class.getName();
    private static final int VOLUME_FLAGS = 0;

    private AudioManager mAudioManager;
    private final int mStreamType;
    private final int mOriginalVolume;
    private final int mMaxVolume;
    private final int mAmpInterval;

    public VolumeAdjuster(AudioManager audioManager, int streamType) {
        mAudioManager = audioManager;
        mStreamType = streamType;
        mOriginalVolume = mAudioManager.getStreamVolume(mStreamType);
        mMaxVolume = mAudioManager.getStreamMaxVolume(mStreamType);
        mAmpInterval = NoiseMeter.METER_LIMIT / mMaxVolume;
    }

    public void adjustVolume(int noiseAmplitude, SensitivityLevel sensitivityLevel) {
        int adjustedVolumeLevel = Math.max(1, noiseAmplitude / mAmpInterval);

        if (sensitivityLevel == SensitivityLevel.LOW) {
            adjustedVolumeLevel = Math.max(1, adjustedVolumeLevel - 1);
        } else if (sensitivityLevel == SensitivityLevel.HIGH) {
            adjustedVolumeLevel = Math.min(mMaxVolume, adjustedVolumeLevel + 1);
        }

        mAudioManager.setStreamVolume(mStreamType, adjustedVolumeLevel, VOLUME_FLAGS);

        Log.d(TAG, "noise amplitude: " + noiseAmplitude);
        Log.d(TAG, "volume level: " + adjustedVolumeLevel);
    }

    public void restoreVolume() {
        mAudioManager.setStreamVolume(mStreamType, mOriginalVolume, VOLUME_FLAGS);
    }

    public void mute() {
        mAudioManager.setStreamVolume(mStreamType, 0, VOLUME_FLAGS);
    }

    public enum SensitivityLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    public static final String SENSITIVITY_PREFS_NAME = "SensitivityPrefs";
    public static final String SENSITIVITY_PREFS_KEY = "sensitivityLevel";

    public static SensitivityLevel getUsersSensitivityLevel(Context context) {
        String enumValueName = context
                .getSharedPreferences(SENSITIVITY_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(SENSITIVITY_PREFS_KEY, VolumeAdjuster.SensitivityLevel.MEDIUM.name());

        // enumValueName may be null, use MEDIUM as default in this case
        SensitivityLevel sensitivityLevel = SensitivityLevel.MEDIUM;
        if (enumValueName != null) {
            sensitivityLevel = Enum.valueOf(VolumeAdjuster.SensitivityLevel.class, enumValueName);
        }

        return sensitivityLevel;
    }

}
