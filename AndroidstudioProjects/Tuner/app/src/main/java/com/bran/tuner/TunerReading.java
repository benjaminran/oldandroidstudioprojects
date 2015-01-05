package com.bran.tuner;

import android.util.Log;

import java.util.HashMap;

public class TunerReading {
    private String noteName;
    private int centsOff;
    private static final String[] PITCH_NAMES = new String[]{ "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G", "Ab" };
//    private HashMap<String, Double> pitches;
    private static String[] pitches;
    static {
        pitches = new String[89];
        for(int i=1, octave=0; i<89; i++) {
            if(i%PITCH_NAMES.length==0) octave++;
            pitches[i] = PITCH_NAMES[(i-5)%PITCH_NAMES.length] + octave;
            Log.i("t", pitches[i]);
        }
    }

    public static TunerReading getReadingFromFrequency(double frequency) {
        double n = 12 * Math.log(frequency/440.0) / Math.log(2.0) + 49.0;
        int pitchNumber = (int) Math.round(n);
        String pitchKey = pitches[pitchNumber-1];
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
