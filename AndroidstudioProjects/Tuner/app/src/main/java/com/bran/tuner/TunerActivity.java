package com.bran.tuner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TunerActivity extends Activity {
    // UI Components
    private Button btn;
    private TunerView tunerView;
    private LinearLayout graphContainer;
    private TextView debugTextView;

    // App components
    private Recorder recorder;

    private TunerReading reading;
    private String debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recorder = Recorder.getInstance(this, TunerActivity.this);
        recorder.start();
    }

    public void onBtnClicked(View v) {
        tunerView.updateReading(reading);
        debugTextView.setText(debugText);
    }

    private void initUi() {
        btn = (Button)  findViewById(R.id.btn);
        tunerView = (TunerView) findViewById(R.id.tunerView);
        graphContainer = (LinearLayout) findViewById(R.id.graphContainer);
        debugTextView = (TextView) findViewById(R.id.debugText);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recorder.release();
        recorder = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tuner, menu);
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

    public void setReading(TunerReading reading) {
        this.reading = reading;
    }

    public void setDebugText(String debugText) {
        this.debugText = debugText;
    }
}
