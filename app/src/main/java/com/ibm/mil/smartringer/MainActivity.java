package com.ibm.mil.smartringer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int POLLING_RATE = 500;

    private Handler mHandler;
    private Runnable mRunnable;
    private Button mTestRingerButton;
    private MediaPlayer mMediaPlayer;
    private NoiseMeter mNoiseMeter;
    private VolumeAdjuster mVolumeAdjuster;
    private VolumeAdjuster.SensitivityLevel mSensitivityLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMediaPlayer = MediaPlayer.create(this, R.raw.emerald_park);

        mNoiseMeter = new NoiseMeter();
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mVolumeAdjuster = new VolumeAdjuster(audioManager, AudioManager.STREAM_MUSIC);

        HandlerThread thread = new HandlerThread("NoiseMeterThread");
        thread.start();
        mHandler = new Handler(thread.getLooper());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                int noiseAmplitude = mNoiseMeter.getMaxAmplitude();
                mVolumeAdjuster.adjustVolume(noiseAmplitude, mSensitivityLevel);
                mHandler.postDelayed(mRunnable, POLLING_RATE);
            }
        };

        mTestRingerButton = (Button) findViewById(R.id.test_ringer_button);
        mTestRingerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMediaPlayer.isPlaying()) {
                    startRingtone();
                } else {
                    stopRingtone();
                }
            }
        });

        setInitialRadioClicked();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRingtone();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mNoiseMeter.stop();
    }

    public void onSensitivityClicked(View view) {
        switch (view.getId()) {
            case R.id.low_button:
                mSensitivityLevel = VolumeAdjuster.SensitivityLevel.LOW;
                break;
            case R.id.med_button:
                mSensitivityLevel = VolumeAdjuster.SensitivityLevel.MEDIUM;
                break;
            case R.id.high_button:
                mSensitivityLevel = VolumeAdjuster.SensitivityLevel.HIGH;
                break;
        }
    }

    public void onSaveClicked(View view) {
        getSharedPreferences(VolumeAdjuster.SENSITIVITY_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(VolumeAdjuster.SENSITIVITY_PREFS_KEY, mSensitivityLevel.name())
                .commit();

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    private void startRingtone() {
        if (!mMediaPlayer.isPlaying()) {
            mTestRingerButton.setText(R.string.stop_test);
            mMediaPlayer.start();
            mNoiseMeter.start();
            mHandler.postDelayed(mRunnable, POLLING_RATE);
        }
    }

    private void stopRingtone() {
        if (mMediaPlayer.isPlaying()) {
            mTestRingerButton.setText(R.string.start_test);
            mMediaPlayer.pause();
            mNoiseMeter.stop();
            mHandler.removeCallbacks(mRunnable);
            mVolumeAdjuster.restoreVolume();
        }
    }

    private void setInitialRadioClicked() {
        mSensitivityLevel = VolumeAdjuster.getUsersSensitivityLevel(this);

        int radioButtonId;
        switch (mSensitivityLevel) {
            case LOW:
                radioButtonId = R.id.low_button;
                break;
            case HIGH:
                radioButtonId = R.id.high_button;
                break;
            default:
                radioButtonId = R.id.med_button;
                break;
        }

        RadioButton radioButton = (RadioButton) findViewById(radioButtonId);
        radioButton.setChecked(true);
    }

}
