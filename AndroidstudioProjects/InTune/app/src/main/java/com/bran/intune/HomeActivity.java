package com.bran.intune;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;


public class HomeActivity extends Activity {
    private static final String TAG = "InTune";
    private PCMAudioRecorder recorder;
    private AudioProcessor audioProcessor;
    private ImageButton recBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);
        recorder = new PCMAudioRecorder();
        audioProcessor = new AudioProcessor(recorder);
        initUI();
    }

    private void initUI() {
        recBtn = (ImageButton) findViewById(R.id.recBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!recorder.isRecording()) {
                    recorder.startRecording();
                }
                else {
                    recorder.stopRecording();
                    drawGraph();
                }
            }
        });
    }

    public void drawGraph() {
        short[] pcmData = recorder.graphGetPcmData();
        int length = recorder.graphGetRead();
        GraphView.GraphViewData[] data = new GraphView.GraphViewData[length];
        for(int i=0; i<length; i++) {
            Log.d(TAG, "" + pcmData[i]);
            data[i] = new GraphView.GraphViewData(i, pcmData[i]);
        }
        GraphViewSeries exampleSeries = new GraphViewSeries(data);
        GraphView graphView = new LineGraphView(this, "PCM Data");
        graphView.addSeries(exampleSeries);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(graphView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activiy, menu);
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
