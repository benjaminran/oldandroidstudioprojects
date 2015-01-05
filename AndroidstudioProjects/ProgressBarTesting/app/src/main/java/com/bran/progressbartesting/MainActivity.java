package com.bran.progressbartesting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    private CustomProgressView meter2;
    private VerticalProgressBar meter1;
    private ProgressBar meter3;

    private ScheduledThreadPoolExecutor timer;
    private double x = 0, y=0, xStep = 0.005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meter1 = (VerticalProgressBar) findViewById(R.id.verticalRatingBar1);
        meter2 = (CustomProgressView) findViewById(R.id.customProgressView);
        //meter3 = (ProgressBar) findViewById(R.id.progressbar);

        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                meter1.setProgress((int) (y*100));
                meter2.setProgress(y);
                //meter3.setProgress((int) (y*100));

                x += xStep;
                y = 0.5 + 0.5*Math.sin(x);
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
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
