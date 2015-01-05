package com.bran.tuner;

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
public class Recorder {
    private static final String TAG = "ARE";
    private static Recorder instance = null;
    private AudioRecord recorder;
    private int minBufferSize;
    private int bufferSize;
    private int samplesPerBuffer;
    private DataFilter filter;
    private int sampleRate;

    private Context context;
    private TunerActivity tunerActivity;


    private Recorder(Context context,
                          TunerActivity tunerActivity) {
        this.context = context;
        this.tunerActivity = tunerActivity;
    }

    private Recorder() {}

    public void start() {
        new Thread(new Runnable() {
            public Handler mHandler;
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();

                sampleRate = 44100;
                minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                bufferSize = 4 * minBufferSize;
                recorder = new AudioRecord(
                        MediaRecorder.AudioSource.DEFAULT,
                        sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize
                );
                filter = new DataFilter(context, tunerActivity, sampleRate, bufferSize);
                recorder.setRecordPositionUpdateListener(filter);
                recorder.setPositionNotificationPeriod(minBufferSize);
                samplesPerBuffer = minBufferSize;
                recorder.startRecording();
                Looper.loop();
            }
        }, "Recording Thread").start();
    }

    // Bug: If getInstance() is called first, context and mainActivity will never be set!
    public static Recorder getInstance(Context context, TunerActivity tunerActivity) {
        if(instance == null) instance = new Recorder(context, tunerActivity);
        return instance;
    }

    public static Recorder getInstance() {
        if (instance == null) instance = new Recorder();
        return instance;
    }

    public Boolean isReady() {
        return AudioRecord.STATE_INITIALIZED==recorder.getState();
    }
    public Boolean isRecording() {
        return AudioRecord.RECORDSTATE_RECORDING==recorder.getRecordingState();
    }

    public void release() {
        if(AudioRecord.RECORDSTATE_RECORDING!=recorder.getRecordingState()) recorder.stop();
        recorder.release();
    }

    public int getSamplesPerBuffer() { return samplesPerBuffer; }

    public int getSampleRate() {
        return sampleRate;
    }
}
