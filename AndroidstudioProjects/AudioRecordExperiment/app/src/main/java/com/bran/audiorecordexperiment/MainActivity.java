package com.bran.audiorecordexperiment;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    private static final String TAG = "ARE";
    public static final int UI_UPDATES_PER_BUFFER = 8;
    private AudioRecorder audioRecorder;
    private FrequencyProcessor frequencyProcessor;
    private Button recBtn, refreshBtn;
    private ArrayList<UiAnimator> uiAnimators;

    //UI Update loop mechanism
    private int timerPeriod;
    private Handler uiTimerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            for(UiAnimator uiAnimator : uiAnimators) uiAnimator.onUiTimerTick();
            uiTimerHandler.postDelayed(this, timerPeriod);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Debug.startMethodTracing("areprofile");

        uiAnimators = new ArrayList<UiAnimator>(2);
        audioRecorder = AudioRecorder.getInstance(this, MainActivity.this);
        frequencyProcessor = new FrequencyProcessor(MainActivity.this);
        audioRecorder.startRecording();

        recBtn = (Button) findViewById(R.id.recBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioRecorder.isRecording()) {
                    //Debug.stopMethodTracing();
                    audioRecorder.stopRecording();
                    recBtn.setText("Resume Recording");
                }
                else {
                    audioRecorder.resumeRecording();
                    recBtn.setText("Stop Recording");
                }
            }
        });

        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioRecorder.refreshData();
            }
        });
    }

    public void processFrequency(double frequency) {
        frequencyProcessor.processFrequency(frequency);
    }

    public void startUiUpdateLoop() {
        timerPeriod = (int) Math.floor(audioRecorder.getSamplesPerBuffer() / audioRecorder.getSampleRateInHz() * UI_UPDATES_PER_BUFFER);
        uiTimerHandler.postDelayed(timerRunnable, 0);
    }

    public void addUiAnimator(UiAnimator animator) { uiAnimators.add(animator); }

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
