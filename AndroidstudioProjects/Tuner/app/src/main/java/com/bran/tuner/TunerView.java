package com.bran.tuner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Beni on 9/29/14.
 */
public class TunerView extends TextView {

    public TunerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateReading(TunerReading reading) {
        setText(reading.getNoteName() + ": " + reading.getCentsOff());
    }
}
