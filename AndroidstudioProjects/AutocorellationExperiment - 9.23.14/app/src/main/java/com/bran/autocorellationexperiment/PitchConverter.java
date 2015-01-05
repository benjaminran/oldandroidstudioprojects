package com.bran.autocorellationexperiment;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Beni on 9/18/14. Make note names editable in preferences?
 */
public class PitchConverter {
    private static final String[] PITCH_NAMES = new String[]{ "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G", "Ab" };
    private HashMap<String, Double> pitches;

    public PitchConverter() {
        initPitches();
    }

    private void initPitches() {
        pitches = new HashMap<String, Double>(88);
        double frequency;
        String pitch;
        int octaveCounter = 0;
        for (int i=0;i<88;i++) {
            if((i-3)%12==0) octaveCounter++;
            frequency = 440 * Math.pow(2.0, (i-48)/12.0);
            pitch = PITCH_NAMES[i%12] + octaveCounter;
            pitches.put(pitch, frequency);
            Log.i("ace", (i+1) + " - " + pitch + " - " + frequency);
        }


    }

    public String getPitchFromFrequency(double frequency) {
        /*double dif = Math.abs(pitches.get(1)-frequency);
        double temp;
        int pitchKey = 0;
        for(int i=1;i<88;i++){
            temp = Math.abs(pitches.get(i+1)-frequency);
            if(temp<dif){
                pitchKey = i+1;
                dif=temp;
            }

        }
        return pitchKey;*/
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
        return pitchKey;
    }

    public double getFrequencyFromPitch(String pitch) { return pitches.get(pitch); }

}
