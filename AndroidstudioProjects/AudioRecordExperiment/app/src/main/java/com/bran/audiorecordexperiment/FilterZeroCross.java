package com.bran.audiorecordexperiment;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Beni on 7/27/14.
 */
public class FilterZeroCross implements DataFilter {
    private static final String TAG = "ARE";
    private static final int PERIODS_RECORDED = 100;
    private MainActivity mainActivity;
    private AudioRecorder audioRecorder;
    private ArrayList<Integer> periods;
    private int period;
    private String printText;

    public FilterZeroCross(MainActivity mainActivity, AudioRecorder audioRecorder) {
        // Init UI
        this.mainActivity = mainActivity;
        this.audioRecorder = audioRecorder;
    }

    @Override
    public void processBuffer(short[] buffer, int read) {
        boolean prevElemPos = buffer[0] >= 0;
        period = 0;
        periods = new ArrayList<Integer>(PERIODS_RECORDED); // Size?
        for(int i=0; i<PERIODS_RECORDED; i++) periods.add(0);
        for (int i = 1; i < read; i++) {
            period++;
            Boolean currentElemPos = buffer[i] >= 0;
            if (!prevElemPos && currentElemPos) addPeriod();
            prevElemPos = currentElemPos;
        }
    }

    private double getFrequencyFromPeriods() { // Err: This is returning a number that is way too high.
        int sum = 0;
        for(int i : periods) sum += i;
        int avgPeriodInSamples = (int) sum/periods.size();
        return audioRecorder.getSampleRateInHz() / avgPeriodInSamples;
    }

    private void addPeriod() {
        periods.remove(0);
        periods.add(period);
        period = 0;

    }

    public void refreshData(){
        double frequency = getFrequencyFromPeriods();
        printText = "";
        for (int i : periods) printText += i + " ";
        mainActivity.processFrequency(frequency);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView zeroesText = (TextView) mainActivity.findViewById(R.id.zeroesText);
                zeroesText.setText(printText);
            }
        });
    }
}
