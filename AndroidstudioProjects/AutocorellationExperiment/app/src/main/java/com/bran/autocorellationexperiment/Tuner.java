package com.bran.autocorellationexperiment;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Beni on 9/18/14. Make note names editable in preferences?
 */
public class Tuner {
    private static final String[] PITCH_NAMES = new String[]{ "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G", "Ab" };
    private HashMap<String, Double> pitches;
    private String[] pitchKeysAsc;

    public Tuner() {
        initPitches();
    }

    private void initPitches() {
        pitches = new HashMap<String, Double>(88);
        pitchKeysAsc = new String[88];
        double frequency;
        String pitch;
        int octaveCounter = 0;
        for (int i=0;i<88;i++) {
            if((i-3)%12==0) octaveCounter++;
            frequency = 440 * Math.pow(2.0, (i-48)/12.0);
            pitch = PITCH_NAMES[i%12] + octaveCounter;
            pitches.put(pitch, frequency);
            pitchKeysAsc[i] = pitch;
            Log.i("ace", (i+1) + " - " + pitch + " - " + frequency);
        }
    }

    public Reading getReadingFromFrequency(double frequency) {
        double n = 12 * Math.log(frequency/440.0) / Math.log(2.0) + 49.0;
        int pitchNumber = (int) Math.round(n);
        String pitchKey = pitchKeysAsc[pitchNumber-1];
        int centsOff = (int) Math.round(100 * (n - pitchNumber));
        return new Reading(pitchKey, centsOff);
        /*// get noteName
        double dif = Math.abs(pitches.get("A0") - frequency);
        double temp;
        String pitchKey = "A0";
        for(Map.Entry<String, Double> entry: pitches.entrySet()) {
            temp = Math.abs(entry.getValue() -frequency);
            if(temp<dif){
                pitchKey = entry.getKey();
                dif=temp;
            }
        }
        //get centsOff
        int centsOff = 0;
        //get sharp
        Boolean sharp = frequency >= pitches.get(pitchKey);
        return new Reading(pitchKey, centsOff, sharp);*/
    }

    public double getFrequencyFromPitch(String pitch) { return pitches.get(pitch); }

    public class Reading {
        private String noteName;
        private int centsOff;

        public Reading(String noteName, int centsOff){
            this.noteName = noteName;
            this.centsOff = centsOff;
        }

        public String getNoteName() { return noteName; }
        public int getCentsOff() { return centsOff; }
    }
}
