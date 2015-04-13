package com.ibm.mil.smartringer;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String PREFS_NAME = "SmartRingerPrefs";
    private static final String SENS_KEY = "sensitivityLevel";

    private static final int POLLING_RATE = 1000;

    private NoiseMeter mNoiseMeter;
    private Handler mHandler;
    private Runnable mRunnable;
    private SensitivityLevel mSensitivityLevel;
    private Button mTestRingerButton;
    private Ringtone mRingtone;
    private AudioManager mAudioManager;
    private int mOriginalVolume;
    private int mMaxVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNoiseMeter = new NoiseMeter();

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Max Amplitude: " + mNoiseMeter.getMaxAmplitude());
                mHandler.postDelayed(this, POLLING_RATE);
            }
        };

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(this, ringtoneUri);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

        mTestRingerButton = (Button) findViewById(R.id.test_ringer_button);
        mTestRingerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRingtone.isPlaying()) {
                    stopRingtone();
                } else {
                    startRingtone();
                }
            }
        });

        setInitialRadioClicked();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNoiseMeter.start();
        mHandler.postDelayed(mRunnable, POLLING_RATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopRingtone();
        mNoiseMeter.stop();
        mHandler.removeCallbacks(mRunnable);
    }

    public void onSensitivityClicked(View view) {
        switch (view.getId()) {
            case R.id.low_button:
                mSensitivityLevel = SensitivityLevel.LOW;
                break;
            case R.id.med_button:
                mSensitivityLevel = SensitivityLevel.MEDIUM;
                break;
            case R.id.high_button:
                mSensitivityLevel = SensitivityLevel.HIGH;
                break;
        }
    }

    public void onSaveClicked(View view) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(SENS_KEY, mSensitivityLevel.getCode())
                .commit();

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    private void setInitialRadioClicked() {
        int code = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getInt(SENS_KEY, SensitivityLevel.MEDIUM.getCode());
        mSensitivityLevel = SensitivityLevel.lookupByCode(code);

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

    private void startRingtone() {
        if (!mRingtone.isPlaying()) {
            mTestRingerButton.setText(R.string.stop_test);
            mOriginalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mMaxVolume, 0);
            mRingtone.play();
        }
    }

    private void stopRingtone() {
        if (mRingtone.isPlaying()) {
            mTestRingerButton.setText(R.string.start_test);
            mRingtone.stop();
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mOriginalVolume, 0);
        }
    }

    private enum SensitivityLevel {
        // do not change code value passed to the enum's constructor as this is used for making
        // an enum value persistable within SharedPreferences
        LOW(1),
        MEDIUM(2),
        HIGH(3);

        private final int mCode;
        private static final Map<Integer, SensitivityLevel> mValuesByCode;

        static {
            mValuesByCode = new HashMap<>();
            for (SensitivityLevel level : values()) {
                mValuesByCode.put(level.mCode, level);
            }
        }

        private SensitivityLevel(int code) {
            mCode = code;
        }

        public static SensitivityLevel lookupByCode(int code) {
            return mValuesByCode.get(code);
        }

        public int getCode() {
            return mCode;
        }
    }

}
