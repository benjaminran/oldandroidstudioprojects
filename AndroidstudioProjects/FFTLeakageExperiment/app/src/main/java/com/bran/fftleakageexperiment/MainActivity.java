package com.bran.fftleakageexperiment;

import android.app.Activity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
    private Recorder recorder;
    private Grapher grapher;

    // FFT Options
    private static final int[] FFT_SIZES = new int[] { 16, 32, 64, 128, 256 };
    private static final int SAMPLE_RATE = 16000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorder = new Recorder(MainActivity.this, SAMPLE_RATE);
        grapher = new Grapher(this, MainActivity.this);
    }

    public void captureData(View button) {
        Log.d("fle", "captureData() Called.");
        ((Button) findViewById(R.id.btn)).setText("Redisplay Data");
        short[] data = recorder.captureData();
        // Graph input data
        int max = 0;
        for(int n : FFT_SIZES) if(n>max) max = n;
        double[] inputData = new double[max];
        for(int i=0;i<inputData.length;i++) inputData[i] = data[i];
        grapher.drawGraph(inputData, "Input", false, 1.0);
        // Graph output data
        for(int n : FFT_SIZES) {
            double[] input = new double[n];
            for(int i=0;i<n;i++) input[i] = data[i];
            performFft(input);
        }
    }

    private void performFft(double[] input) {
        FFT fft = new FFT(input.length);
        double[] output = new double[input.length];
        fft.fft(input, output);
        onFftResult(output);
    }

    public void onFftResult(double[] output) {
        double baseAnalysisFrequency = SAMPLE_RATE/output.length;
        int noteIndex = (int) Math.round(440/baseAnalysisFrequency);
        grapher.drawGraph(output, "N = " + output.length + "; 440Hz component (" + noteIndex*baseAnalysisFrequency + "Hz): " + output[noteIndex], true, baseAnalysisFrequency);
        //grapher.drawTextView();
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
