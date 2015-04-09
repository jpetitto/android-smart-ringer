package com.ibm.mil.smartringer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String PREFS_NAME = "SmartRingerPrefs";
    private static final String SENS_KEY = "sensitivityLevel";

    private static final int POLLING_RATE = 1000;

    private static final int LOW_SENS = 1;
    private static final int MED_SENS = 2;
    private static final int HIGH_SENS = 3;

    private NoiseMeter mNoiseMeter;
    private Handler mHandler;
    private Runnable mRunnable;
    private int mSensitivityLevel;

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

        mNoiseMeter.stop();
        mHandler.removeCallbacks(mRunnable);
    }

    public void onSensitivityClicked(View view) {
        switch (view.getId()) {
            case R.id.low_button:
                Log.i(TAG, "Low sensitivity");
                mSensitivityLevel = LOW_SENS;
                break;
            case R.id.med_button:
                Log.i(TAG, "Medium sensitivity");
                mSensitivityLevel = MED_SENS;
                break;
            case R.id.high_button:
                Log.i(TAG, "High sensitivity");
                mSensitivityLevel = HIGH_SENS;
                break;
        }
    }

    public void onSaveClicked(View view) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(SENS_KEY, mSensitivityLevel)
                .commit();

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    public void onTestClicked(View view) {
        Log.i(TAG, "Test noise sensitivity...");
    }

    private void setInitialRadioClicked() {
        mSensitivityLevel = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getInt(SENS_KEY, MED_SENS);

        int radioButtonId;
        switch(mSensitivityLevel) {
            case LOW_SENS:
                radioButtonId = R.id.low_button;
                break;
            case HIGH_SENS:
                radioButtonId = R.id.high_button;
                break;
            default:
                radioButtonId = R.id.med_button;
        }

        RadioButton radioButton = (RadioButton) findViewById(radioButtonId);
        radioButton.setChecked(true);
    }

}
