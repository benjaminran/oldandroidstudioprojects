package com.bran.audiorecordexperiment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Beni on 7/28/14.
 */
public class FilterAmplitude implements DataFilter, UiAnimator {
    private static final String TAG = "ARE";
    private static final int AMPLITUDES_RECORDED_PER_BUFFER = MainActivity.UI_UPDATES_PER_BUFFER;
    public static final float OPTIMUM_AMPLITUDE = Short.MAX_VALUE / 2; // idk
    public static final float MAX_AMPLITUDE = Short.MAX_VALUE; // idk
    private static final float OPT_MAX_AMPLITUDE_DIF = MAX_AMPLITUDE - OPTIMUM_AMPLITUDE;
    private static final float GREEN_HUE = 120;
    private static final float RED_HUE = 0;
    private MainActivity mainActivity;
    private Context context;
    private LowPassFilter lowPassFilter;
    private AmplitudeMeterView amplitudeMeter;
    private float newColorHue;
    private int newColor;
    private short[] amplitudes;
    private int amplitudeIndex;

    public FilterAmplitude(MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;
        lowPassFilter = new LowPassFilter();
        amplitudes = new short[AMPLITUDES_RECORDED_PER_BUFFER];
        amplitudeIndex = 0;
        mainActivity.addUiAnimator(this);
        showMeter();
    }

    private void showMeter() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                amplitudeMeter = (AmplitudeMeterView) mainActivity.findViewById(R.id.amplitudeMeter);
                amplitudeMeter.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void processBuffer(short[] buffer, int read) {
        int recordedAmplitudePeriod = (int) Math.floor(read/AMPLITUDES_RECORDED_PER_BUFFER);
        if(amplitudeIndex!=0) Log.e(TAG, "Amplitude data lost!");
        for(int i=0; i<AMPLITUDES_RECORDED_PER_BUFFER; i++) {
            int j = i * recordedAmplitudePeriod;
            short maxValue = buffer[j];
            j++;
            while (buffer[j] < maxValue) { // Skips forward to an ascending part of the curve
                maxValue = buffer[j];
                j++;
            }
            while (buffer[j] > maxValue) { //finds last amplitude before values begin descending again
                maxValue = buffer[j];
                j++;
            }
            if (maxValue < 0) maxValue = 0;
            maxValue = lowPassFilter.filter(maxValue);
            amplitudes[i] = maxValue;
            //updateDisplay(maxValue);
        }
    }

    @Override
    public void refreshData() {/*Intentionally blank because no debug data is presented in the activity*/}

    /**
     * Tells the homeActivity to update the amplitude display bar with the next amplitude.
     * Converts the amplitude to percent?
     */
    private void updateDisplay(short amplitude) {
        newColorHue = GREEN_HUE - (Math.abs(amplitude - OPTIMUM_AMPLITUDE) / OPT_MAX_AMPLITUDE_DIF) * GREEN_HUE;
        newColor = Color.HSVToColor(new float[]{ newColorHue, 1f, 1f});
        final double amplitudeFraction = (double) amplitude / Short.MAX_VALUE;
        //Log.i(TAG, "" + amplitude + " / " + Short.MAX_VALUE + " = "  + amplitudeFraction);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                amplitudeMeter.setColor(newColor);
                amplitudeMeter.setAmplitude(amplitudeFraction);
            }
        });
    }

    @Override
    public void onUiTimerTick() {
        updateDisplay(amplitudes[amplitudeIndex]);
        amplitudeIndex = (amplitudeIndex + 1) & AMPLITUDES_RECORDED_PER_BUFFER;
    }

    public class LowPassFilter {
        private static final int BUFFER_SIZE = 8;
        private ArrayList<Short> rawAmplitudes;
        private int sum;

        public LowPassFilter(){
            sum = 0;
            rawAmplitudes = new ArrayList<Short>(BUFFER_SIZE);
            for(int i=0; i<BUFFER_SIZE; i++) rawAmplitudes.add(new Short((short) 0));
        }

        public short filter(short newReading) {
            sum -= rawAmplitudes.get(0);
            sum += newReading;
            rawAmplitudes.remove(0);
            rawAmplitudes.add(new Short(newReading));
            /*if((short) (sum/BUFFER_SIZE) < 0) {
                Log.i(TAG, "Filtered Amplitude: " + sum/BUFFER_SIZE);
                for(Short s : rawAmplitudes) Log.i(TAG, "" + s);
            }
            short rasum = 0;
            for(short s:rawAmplitudes) rasum += s;
            if(sum!=rasum) Log.i(TAG, "" + rasum + " " + sum);*/
            return (short) (sum/BUFFER_SIZE);
        }

    }
}
