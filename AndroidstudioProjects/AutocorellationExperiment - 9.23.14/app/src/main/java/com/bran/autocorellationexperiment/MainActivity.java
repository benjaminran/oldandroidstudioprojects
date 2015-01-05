package com.bran.autocorellationexperiment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private Recorder recorder;
    private Grapher grapher;
    private PitchConverter pitchConverter;

    // Autocorrelation  Options
    //private static final int[] FFT_SIZES = new int[] { 16, 32, 64, 128, 256 };
    private static final int INPUT_SIZE = 2048;
    public static final int SAMPLE_RATE = 44100;

    // PDA Options
    private static final double PEAK_THRESHOLD_PERCENT = 0.75;
    private static final int MAX_PERIOD_IN_SAMPLES = (int) Math.ceil(SAMPLE_RATE / 27.50);
    private static final int MIN_PERIOD_IN_SAMPLES = (int) Math.floor(SAMPLE_RATE / 4186.01);
    private static final int AUTOCORRELATION_WINDOW = 2*MAX_PERIOD_IN_SAMPLES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("ace", "PDA period range (in samples): " + MIN_PERIOD_IN_SAMPLES + " to " + MAX_PERIOD_IN_SAMPLES);

        recorder = new Recorder(MainActivity.this, SAMPLE_RATE, INPUT_SIZE);
        grapher = new Grapher(this, MainActivity.this);
        pitchConverter = new PitchConverter();
    }

    public void captureData(View button) {
        Log.d("fle", "captureData() Called.");
        ((Button) findViewById(R.id.btn)).setText("Redisplay Data");
        short[] data = recorder.captureData();
        // Graph input data
        double[] inputData = new double[INPUT_SIZE];
        for(int i=0;i<inputData.length;i++) inputData[i] = data[i];
        grapher.drawGraph(inputData, "Input", false);

        // Graph output data
        double[] ac1 = performAutocorrelation(inputData, 0);
        //double[] ac2 = performAutocorrelation(ac1, 1); // Size too small for re-auto-correlation
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
    // TODO: Average period between each AC peak to get more accurate input period.
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
        grapher.drawGraph(output, "Frequency: " + frequency + "; Note Name: " + pitchConverter.getPitchFromFrequency(frequency), true);
        String text="Peak values: ";
        for(int peakIndex : peaks) text+="<br>" + output[peakIndex];
        grapher.drawText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
