package com.bran.tunerviewexperiment;

import android.util.Log;

import java.util.HashMap;

public class TunerReading {
    private String noteName;
    private int centsOff;
    private static final String[] PITCH_NAMES = new String[]{ "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G", "Ab" };
    private HashMap<String, Double> pitches;
    private static String[] pitchKeysAsc; // Initialized with empty first value to allow for retrieving pitch via note number easily
    static {
        pitchKeysAsc = new String[89];
        for (int i=1, oct=0;i<=88;i++) {
            if((i-4)%PITCH_NAMES.length==0) oct++;
            pitchKeysAsc[i] = PITCH_NAMES[i%PITCH_NAMES.length] + oct;
        }
        String text = "";
        for(String s : pitchKeysAsc) text += s + "\n";
        Log.i("tve", text);
    }

    public TunerReading getReadingFromFrequency(double frequency) {
        double n = 12 * Math.log(frequency/440.0) / Math.log(2.0) + 49.0;
        int pitchNumber = (int) Math.round(n);
        String pitchKey = pitchKeysAsc[pitchNumber-1];
        int centsOff = (int) Math.round(100 * (n - pitchNumber));
        return new TunerReading(pitchKey, centsOff);
    }

    public TunerReading(String noteName, int centsOff){
        this.noteName = noteName;
        this.centsOff = centsOff;
    }

    public String getNoteName() { return noteName; }
    public int getCentsOff() { return centsOff; }
}

