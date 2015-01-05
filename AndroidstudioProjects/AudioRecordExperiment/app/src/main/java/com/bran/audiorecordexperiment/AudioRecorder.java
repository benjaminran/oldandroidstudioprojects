package com.bran.audiorecordexperiment;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

/**
 * Created by Beni on 7/30/14.
 */
public class AudioRecorder implements AudioRecord.OnRecordPositionUpdateListener {
    private static final String TAG = "ARE";
    private static AudioRecorder instance = null;
    private AudioRecord recorder;
    private double secondsPerSample;
    private short[] buffer;
    private int samplesPerBuffer;
    private ArrayList<DataFilter> filters;
    private int minBufferSize;
    private int sampleRateInHz;
    private Boolean firstTime;

    private Context context;
    private MainActivity mainActivity;


    private AudioRecorder(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        firstTime = true;
    }

    private AudioRecorder() {}

    public void startRecording() {
        new Thread(new Runnable() {
            public Handler mHandler;
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();

                filters = new ArrayList<DataFilter>();
                filters.add(new FilterZeroCross(mainActivity, instance));
                filters.add(new FilterGrapher(context, mainActivity));
                filters.add(new FilterAmplitude(mainActivity, context));
                sampleRateInHz = 44100;
                minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                buffer = new short[minBufferSize*4];
                secondsPerSample = 1.0 / sampleRateInHz;
                recorder = new AudioRecord(
                        MediaRecorder.AudioSource.DEFAULT,
                        sampleRateInHz,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        minBufferSize*4
                );
                recorder.setRecordPositionUpdateListener(instance);
                recorder.setPositionNotificationPeriod(minBufferSize);
                samplesPerBuffer = minBufferSize;
                //recorder.startRecording();
                resumeRecording();
                Looper.loop();
            }
        }, "Recording Thread").start();
    }

    public void resumeRecording() {
        recorder.startRecording();
    }

    // Bug: If getInstance() is called first, context and mainActivity will never be set!
    public static AudioRecorder getInstance(Context context, MainActivity mainActivity) {
        if(instance == null) instance = new AudioRecorder(context, mainActivity);
        return instance;
    }

    public static AudioRecorder getInstance() {
        if(instance == null) instance = new AudioRecorder();
        return instance;
    }

    // (Should be being called on Recording Thread, not UI thread, b/c AudioRecord was instantiated on Recording Thread)
    @Override
    public void onPeriodicNotification(AudioRecord audioRecord) {
        int read = audioRecord.read(buffer, 0, minBufferSize * 2);// recorder?
        for(int i=0, limit=filters.size(); i<limit; i++) {
            filters.get(i).processBuffer(buffer, read);
        }
        if(firstTime) {
            mainActivity.startUiUpdateLoop();
            firstTime=false;
        }

    }

    public void refreshData() {
        for(DataFilter filter : filters) filter.refreshData();
    }

    public Boolean isReady() {
        return AudioRecord.STATE_INITIALIZED==recorder.getState();
    }
    public Boolean isRecording() {
        return AudioRecord.RECORDSTATE_RECORDING==recorder.getRecordingState();
    }
    public void stopRecording() {
        recorder.stop();
    }
    public void release() {
        recorder.release();
    }

    @Override
    public void onMarkerReached(AudioRecord audioRecord) {}

    public int getSamplesPerBuffer() { return samplesPerBuffer; }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }
}

