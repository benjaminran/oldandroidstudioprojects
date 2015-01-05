package com.bran.fftexperiment;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Beni on 8/28/14.
 */
public class Recorder {
    private AudioRecord recorder;

    // AudioRecord options
    private static final int SAMPLE_RATE = MainActivity.SAMPLE_RATE;
    private int bufferSize;
    private short[] pcmData;

    public void record(final MainActivity mainActivity) {
        bufferSize = 4 * AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.d("FFTE", "Buffer size: " + bufferSize);
        pcmData = new short[bufferSize];
        recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        recorder.setNotificationMarkerPosition(bufferSize/2);
        recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioRecord audioRecord) {
                for (short s : pcmData) s = 1;
                int read = recorder.read(pcmData, 0, bufferSize);
                Log.d("FFTE", "read: " + read);
                Log.d("FFTE", "state: " + recorder.getState() + " - " + recorder.getRecordingState());
                String text= "";
                for (int i = 0; i < pcmData.length; i++) {
                    text += pcmData[i] + " ";
                }
                Log.d("FFTE", "PCM Data: " + text);
                recorder.stop();
                recorder.release();
                mainActivity.beginProcessing();
            }
            @Override
            public void onPeriodicNotification(AudioRecord audioRecord) {}
        });
        recorder.startRecording();
    }

    public short[] getPcmData() {
        return pcmData;
    }
}
