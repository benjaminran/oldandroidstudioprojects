package com.bran.fftleakageexperiment;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by Beni on 9/11/14.
 */
public class Recorder implements AudioRecord.OnRecordPositionUpdateListener{

    private AudioRecord recorder;
    private int audioSource;
    private int sampleRate;
    private int channelConfig;
    private int audioFormat;
    private int bufferSize;
    private short[] buffer;
    private int bufferIndex;

    public Recorder(MainActivity mainActivity, int sampleRate) {
        audioSource = MediaRecorder.AudioSource.MIC;
        this.sampleRate = sampleRate;
        channelConfig = AudioFormat.CHANNEL_IN_MONO;
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        bufferSize = 4 * AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        recorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);

        buffer = new short[bufferSize];
        bufferIndex = 0;

        recorder.setPositionNotificationPeriod(bufferSize/4);
        recorder.setRecordPositionUpdateListener(this);
        recorder.startRecording();
    }

    public short[] captureData() {
        recorder.stop();
        short[] data = new short[buffer.length];
        for(int i=0;i<buffer.length;i++) data[i] = buffer[(bufferIndex+i)%buffer.length];
        return data;
    }

    @Override
    public void onMarkerReached(AudioRecord audioRecord) {}

    @Override
    public void onPeriodicNotification(AudioRecord audioRecord) {
        recorder.read(buffer, bufferIndex, bufferSize/2);
        bufferIndex = (bufferIndex<bufferSize) ? bufferIndex +1 : 0;
        //recorder.release();
    }
}
