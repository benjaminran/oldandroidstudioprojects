package com.bran.intune;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Beni on 7/23/14.
 */
public class PCMAudioRecorder {
    private static final String TAG = "InTune";
    private static final float MIN_NOTE_DURATION_SEC = 1/8;

    private Context context;

    private AudioRecord recorder;
    private int sampleRate;
    private final static int[] SAMPLE_RATES = {44100, 22050, 11025, 8000};
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeBytes;

    private RecordingThread recordingThread;

    private int expectedSamplesToProcess;
    private int bytesPerElement;
    private static final long POLLING_PERIOD_MILLIS = 1000/8;
    private Boolean isRecording;

    private int graphRead;
    private short[] buffer;

    public PCMAudioRecorder() {
        isRecording = false;
        sampleRate = 44100; // TODO: vary sample rate to optimize performance (check availability first)
        bytesPerElement = (AUDIO_FORMAT==AudioFormat.ENCODING_PCM_16BIT) ? 2 : 1;
        expectedSamplesToProcess = (int) (POLLING_PERIOD_MILLIS * sampleRate * bytesPerElement);
        bufferSizeBytes = Math.max(2*expectedSamplesToProcess, AudioRecord.getMinBufferSize(sampleRate, CHANNEL_CONFIG, AUDIO_FORMAT));
        Log.d(TAG, "Buffer Size: " + bufferSizeBytes);

        recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                CHANNEL_CONFIG, AUDIO_FORMAT,
                bufferSizeBytes
        );

        /*recordingThread = new RecordingThread(recorder, bufferSizeBytes, new DataFilter[]{ *//*new FilterAmplitude(), *//*new FilterZeroCross()}); // TODO: get user's preferred Filter type from SharedPreferences as specified in settings page
        recorder.setRecordPositionUpdateListener(recordingThread, recordingThread.getHandler());
        int result = recorder.setPositionNotificationPeriod(expectedSamplesToProcess);
        if(result==AudioRecord.SUCCESS) Log.i(TAG, "AudioRecord notification period set successfully");
        else if(result==AudioRecord.ERROR_INVALID_OPERATION) Log.e(TAG, "Error setting AudioRecord notification period")*/;
    }

    public void startRecording() {
        recorder.startRecording();
        isRecording = true;
    }

    public short[] graphGetPcmData() { return buffer; }
    public int graphGetRead() { return graphRead; }

    public void stopRecording() {
        recorder.stop();
        isRecording = false;
    }

    public Boolean isRecording() {
        return isRecording;
    }
}
