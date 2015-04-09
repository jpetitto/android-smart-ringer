package com.ibm.mil.smartringer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final int POLLING_RATE = 1000;

    private NoiseMeter mNoiseMeter;
    private Handler mHandler;
    private Runnable mRunnable;

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
                break;
            case R.id.med_button:
                Log.i(TAG, "Medium sensitivity");
                break;
            case R.id.high_button:
                Log.i(TAG, "High sensitivity");
                break;
        }
    }

    public void onNoiseTestClicked(View view) {
        Log.i(TAG, "Test noise sensitivity...");
    }

}
