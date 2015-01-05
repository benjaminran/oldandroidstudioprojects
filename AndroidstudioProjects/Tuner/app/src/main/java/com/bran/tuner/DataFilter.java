package com.bran.tuner;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Beni on 7/29/14.
 */
public class DataFilter implements AudioRecord.OnRecordPositionUpdateListener {
    private static final double PEAK_THRESHOLD_PERCENT = 0.75;
    private int minPeriodInSamples;
    private int maxPeriodInSamples;

    private Context context;
    private TunerActivity tunerActivity;

    private int sampleRate;
    private short[] buffer;

    public DataFilter(Context context,TunerActivity tunerActivity, int sampleRate, int bufferSize) {
        this.context = context;
        this.tunerActivity = tunerActivity;
        this.sampleRate = sampleRate;
        maxPeriodInSamples = (int) Math.ceil(sampleRate / 27.50);
        minPeriodInSamples = (int) Math.floor(sampleRate / 4186.01);
        buffer = new short[bufferSize];
    }

    @Override
    public void onPeriodicNotification(AudioRecord audioRecord) {
        int read = audioRecord.read(buffer, 0, buffer.length/2);

        double[] output = performAutoCorrelation(read);
        onResult(output);

        /*if(firstTime) {
            mainActivity.startUiUpdateLoop();
            firstTime=false;
        }*/

    }

    private double[] performAutoCorrelation(int read) {
        //double[] output = new double[input.length];
        ArrayList<Double> output = new ArrayList<Double>(read);
        if(maxPeriodInSamples>read) throw new RuntimeException("Amount of data read into buffer (" + read + ") insufficient for PDA.");
        for(int lag=minPeriodInSamples;lag<=maxPeriodInSamples;lag++){
            double sum = 0;
            for(int i=0;i<read-lag;i++){
                sum += buffer[i]*buffer[i+lag];
            }
            output.add(sum/(read-lag));
        }
        double[] outputArray = new double[output.size()];
        for(int i=0;i<output.size();i++) outputArray[i] = output.get(i);
        return outputArray;
    }

    // TODO: Define a fixed-width window over which to the run AC. 2x the max period?
    private void onResult(double[] output) {
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
        double period = ((double) peaks.get(peaks.size()-1)) / peaks.size() / sampleRate;
        double frequency = 1.0 / period;
        TunerReading reading = TunerReading.getReadingFromFrequency(frequency);
        String noteName = reading.getNoteName();
        int centsOff = reading.getCentsOff();
        String text="Peak values: ";
        for(int peakIndex : peaks) text+="<br>" + output[peakIndex];
        tunerActivity.setReading(reading);
        tunerActivity.setDebugText(text);

    }

    @Override
    public void onMarkerReached(AudioRecord audioRecord){}
}
