package com.bran.intune;

import android.util.Log;

/**
 * Created by Beni on 7/27/14.
 */
public class FilterZeroCross implements DataFilter {
    private static final String TAG = "InTune";

    @Override
    public void processBuffer(short[] buffer, int read) {
        boolean prevElemPos = buffer[0] >= 0;
        int zeroPeriod = 0;
        int[] zeroes = new int[100]; // Size?
        int j=0;
        for(int i=1; i<read; i++) {
            short pcmElement = buffer[i];
            Log.d(TAG, "" + buffer[i]);
            zeroPeriod++;
            Boolean currentElemPos = buffer[i] >= 0;
            if(prevElemPos!=currentElemPos) { // zero crossed
                zeroes[j] = zeroPeriod;
                j++;
                Log.d(TAG, "Zero period: " + zeroPeriod);
                zeroPeriod = 0;
            }
        }
    }
}
