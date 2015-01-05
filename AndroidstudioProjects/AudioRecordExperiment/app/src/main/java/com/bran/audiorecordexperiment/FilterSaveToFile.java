package com.bran.audiorecordexperiment;

import android.content.Context;

/**
 * Created by Beni on 7/27/14.
 */
public class FilterSaveToFile implements DataFilter {
    private static final String TAG = "InTune";
    private Context context;
    private int amplitude;

    public FilterSaveToFile(Context context) {
        this.context = context;
    }

    @Override
    public void processBuffer(short[] buffer, int read) {
        // TODO: Save recorded data to a file
    }

    @Override
    public void refreshData() {

    }
}
