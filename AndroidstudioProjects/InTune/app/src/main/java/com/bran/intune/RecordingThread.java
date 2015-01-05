package com.bran.intune;

import android.media.AudioRecord;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Beni on 7/27/14.
 */
public class RecordingThread extends Thread implements AudioRecord.OnRecordPositionUpdateListener {
    private static final String TAG = "InTune";
    private Handler mHandler;
    private AudioRecord recorder;
    private short[] buffer;
    private int requestSize;
    private DataFilter[] filters;

    // TODO: Ensure the desired parts (esp. the filters) are running on this thread, not the UI thread
    public RecordingThread(AudioRecord recorder, int requestSize, DataFilter[] filters) {
        mHandler = new Handler();
        this.recorder = recorder;
        this.requestSize = requestSize;
        this.filters = filters;
    }

    @Override
    public void run() {

    }

    @Override
    public void onPeriodicNotification(AudioRecord audioRecord) {
        buffer = new short[requestSize];
        int read = recorder.read(buffer, 0, requestSize);
        int dif = requestSize-read;
        if(dif!=0) Log.d(TAG, "Skipped bytes: " + dif);
        for(DataFilter filter : filters) filter.processBuffer(buffer, read);
    }

    @Override
    public void onMarkerReached(AudioRecord audioRecord) {

    }

    public Handler getHandler() {
        return mHandler;
    }
}
