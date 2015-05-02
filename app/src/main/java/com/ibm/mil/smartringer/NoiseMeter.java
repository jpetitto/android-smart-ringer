package com.ibm.mil.smartringer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public final class NoiseMeter {
    public static final int METER_LIMIT = 32768;

    private static final int SAMPLE_RATE = 8000;
    private static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private AudioRecord mAudioRecord;

    public void start() {
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, MIN_BUFFER_SIZE);
        mAudioRecord.startRecording();
    }

    public void stop() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public int getMaxAmplitude() {
        short[] buffer = new short[MIN_BUFFER_SIZE];

        if (mAudioRecord != null) {
            mAudioRecord.read(buffer, 0, MIN_BUFFER_SIZE);
        }

        int max = 0;
        for (short s : buffer) {
            int abs = Math.abs(s);
            if (abs > max) {
                max = abs;
            }
        }

        return max;
    }

}
