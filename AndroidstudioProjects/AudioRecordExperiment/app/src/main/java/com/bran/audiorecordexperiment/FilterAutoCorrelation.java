package com.bran.audiorecordexperiment;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Beni on 9/29/14.
 */
public class FilterAutoCorrelation implements DataFilter{
    // PDA Options
    private static final double PEAK_THRESHOLD_PERCENT = 0.75;
    private int maxPeriodInSamples;
    private int minPeriodInSamples = (int) Math.floor(sampleRate / 4186.01);

    private Context context;
    private MainActivity mainActivity;
    private int samplerate;


    public FilterAutoCorrelation(Context context,MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;


        maxPeriodInSamples = (int) Math.ceil(sampleRate / 27.50);
    }
    @Override
    public void processBuffer(short[] buffer, int read) {

    }

    private double[] performAutocorrelation(double[] input, int acNumber) {
        //double[] output = new double[input.length];
        ArrayList<Double> output = new ArrayList<Double>(input.length);
        if(MAX_PERIOD_IN_SAMPLES>input.length) throw new RuntimeException("Buffer size (" + input.length + ") insufficient for PDA.");
        for(int lag=MIN_PERIOD_IN_SAMPLES;lag<=MAX_PERIOD_IN_SAMPLES;lag++){
            double sum = 0;
            for(int i=0;i<input.length-lag;i++){
                sum += input[i]*input[i+lag];
            }
            output.add(sum/(input.length-lag));
        }
        double[] outputArray = new double[output.size()];
        for(int i=0;i<output.size();i++) outputArray[i] = output.get(i);
        onResult(outputArray, acNumber);
        return outputArray;
    }

    // TODO: Define a fixed-width window over which to the run AC. 2x the max period?
    public void onResult(double[] output, int acNumber) {
        // Get max value and peakThreshold
        double max = 0;
        for(double d: output) if(d>max) max=d;
        double peakThreshold = PEAK_THRESHOLD_PERCENT * max;
        // Get all peak values.
        ArrayList<Integer> peaks = new ArrayList<Integer>();
        for(int i=1;i+1<output.length;i++) if(output[i]>peakThreshold && output[i]>output[i-1] && output[i]>output[i+1]) peaks.add(i);
        // Get index of first peak in output (after output[0])
        int maxIndex = 0;//MIN_PERIOD_IN_SAMPLES;
        while(output[maxIndex+1]<output[maxIndex]) maxIndex++;
        while(output[maxIndex+1]>output[maxIndex]) maxIndex++;
        Log.i("ace", "MaxIndex: " + maxIndex);

        // Convert period in samples to period in second then get frequency
        double period = ((double) peaks.get(peaks.size()-1)) / peaks.size() / SAMPLE_RATE;
        double frequency = 1.0 / period;
        Tuner.Reading  reading = tuner.getReadingFromFrequency(frequency);
        String noteName = reading.getNoteName();
        int centsOff = reading.getCentsOff();
        String sharpOrFlat = centsOff>0 ? "sharp" : "flat";
        grapher.drawGraph(output, "Frequency: " + frequency + "; Reading: " + noteName + " - " + Math.abs(centsOff) + " cents " + sharpOrFlat, true);
        String text="Peak values: ";
        for(int peakIndex : peaks) text+="<br>" + output[peakIndex];
        grapher.drawText(text);
    }

    @Override
    public void refreshData() {

    }
}
