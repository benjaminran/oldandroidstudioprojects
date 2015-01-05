package com.bran.intune;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Beni on 7/28/14.
 */
public class FilterAmplitude implements DataFilter {
    private static final String TAG = "InTune";
    public static final float OPTIMUM_AMPLITUDE = 0; // idk
    public static final float MAX_AMPLITUDE = 0; // idk
    private static final float OPT_MAX_AMPLITUDE_DIF = MAX_AMPLITUDE - OPTIMUM_AMPLITUDE;
    private static final float GREEN_HUE = 120;
    private static final float RED_HUE = 0;
    private HomeActivity homeActivity;
    private float newColorHue;
    private int newColor;

    public FilterAmplitude(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void processBuffer(short[] buffer, int read) {
        int amplitude = (buffer[0] & 0xff) << 8 | buffer[1];
        amplitude = Math.abs(amplitude);
        updateDisplay(amplitude);
        Log.i(TAG, "Amplitude: " + amplitude);
    }

    /**
     * Tells the homeActivity to update the amplitude display bar with the next amplitude.
     * Converts the amplitude to percent?
     */
    private void updateDisplay(int amplitude) {
        newColorHue = GREEN_HUE - (Math.abs(amplitude - OPTIMUM_AMPLITUDE) / OPT_MAX_AMPLITUDE_DIF) * GREEN_HUE;
        newColor = Color.HSVToColor(new float[]{ newColorHue, 1f, 1f});
        homeActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeActivity.getWindow().getDecorView().setBackgroundColor(newColor); // TODO: create custom AmplitudeMeter view and update that
            }
        });
    }
}
