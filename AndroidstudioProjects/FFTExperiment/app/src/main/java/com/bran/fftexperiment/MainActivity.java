package com.bran.fftexperiment;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends Activity {
    private Boolean firstTouch;

    // FFT Options
    public static final int SAMPLE_RATE = 44100; // Hz
    public static final int[] Ns= new int[]{ 32, 64, 128 };
    //private static final double BASE_ANALYSIS_FREQUENCY = SAMPLE_RATE / N; // = Fs/N
    private double[] input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = new double[maxN()];
        firstTouch = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("FFTE", "Touch event registered.");
        if(firstTouch) {
            ((LinearLayout) findViewById(R.id.container)).removeAllViews();
            new Recorder().record(MainActivity.this);
            firstTouch = false;
        }
        return true;
    }

    // Called once input array has been populated with recorded data
    public void beginProcessing() {
        Grapher grapher = new Grapher(this, MainActivity.this);
        grapher.graph(input, "Input Data", true);
        for (int n : Ns) {
            double[] output = new double[n];
            double baseAnalysisFrequency = SAMPLE_RATE / n; // = Fs/N
            new FFT(n).fft(input, output);
            grapher.graph(output, "Output: n = " + n + "; Fs/n = " + baseAnalysisFrequency, false, baseAnalysisFrequency);
        }
    }

    private int maxN() {
        int max = 0;
        for(int n : Ns) if(n>max) max = n;
        return max;
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
