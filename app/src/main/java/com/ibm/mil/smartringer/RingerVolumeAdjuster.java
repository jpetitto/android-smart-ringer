package com.ibm.mil.smartringer;

import android.media.AudioManager;

public final class RingerVolumeAdjuster {
    private NoiseMeter mNoiseMeter;
    private AudioManager mAudioManager;
    private final int mOriginalVolume;
    private final int mMaxVolume;
    private final int mAmpInterval;

    public RingerVolumeAdjuster(NoiseMeter noiseMeter, AudioManager audioManager) {
        mNoiseMeter = noiseMeter;
        mAudioManager = audioManager;

        mOriginalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        mAmpInterval = NoiseMeter.METER_LIMIT / mMaxVolume;
    }

    public void adjustVolume(SensitivityLevel sensitivityLevel) {
        int maxAmplitude = mNoiseMeter.getMaxAmplitude();
        int adjustedVolumeLevel = Math.max(1, maxAmplitude / mAmpInterval);

        if (sensitivityLevel == SensitivityLevel.LOW) {
            adjustedVolumeLevel = Math.max(1, adjustedVolumeLevel - 1);
        } else if (sensitivityLevel == SensitivityLevel.HIGH) {
            adjustedVolumeLevel = Math.min(mMaxVolume, adjustedVolumeLevel + 1);
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, adjustedVolumeLevel, 0);
    }

    public void restoreVolume() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mOriginalVolume, 0);
    }

}
