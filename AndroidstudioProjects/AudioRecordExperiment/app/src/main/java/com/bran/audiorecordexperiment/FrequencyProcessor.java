package com.bran.audiorecordexperiment;

import android.text.Html;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Beni on 8/17/14.
 */
public class FrequencyProcessor {
    private static final Character[] NOTE_NAMES = new Character[]{'A','B','C','D','E','F','G'};
    private String[] pitches;
    private MainActivity mainActivity;

    public FrequencyProcessor(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        pitches = new String[88];
        int octaveCounter = 0;
        String noteName;
        for(int i=0; i<pitches.length; i++) {
            noteName = NOTE_NAMES[i%NOTE_NAMES.length].toString();
            if (noteName.equals("C")) octaveCounter++;
            pitches[i] = noteName + octaveCounter;
        }
    }

    public void processFrequency(double frequency) {
        double pitchNumber = 12 *  Math.log(frequency/440.0) / Math.log(2) + 49;
        final String printText = "Pitch Number: " + pitchNumber + "; Pitch: " + getPitchFromNumber((int) pitchNumber);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView zeroesText = (TextView) mainActivity.findViewById(R.id.pitchText);
                zeroesText.setText(Html.fromHtml(printText));
            }
        });

    }

    private String getPitchFromNumber(int number) {
        return pitches[number - 1];
    }
}
