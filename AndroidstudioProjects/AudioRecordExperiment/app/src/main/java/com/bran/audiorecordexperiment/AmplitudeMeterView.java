package com.bran.audiorecordexperiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Beni on 8/12/14.
 */
public class AmplitudeMeterView extends View {
    private static final double MIN_AMPLITUDE = 0.1;
    private Paint mShadowPaint, mMainPaint;
    private int mColor;
    private int height, width;
    private double progress; // 1 full; 0 empty

    public AmplitudeMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, (float) (height*(1-progress)), width, height, mMainPaint);
    }

    public void setAmplitude(double f) {
        progress=f;
        postInvalidate();
    }

    public void setColor(int color) {
        mMainPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        width = w;
    }

    private void init() {
        mColor = Color.BLUE;
        mMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMainPaint.setStyle(Paint.Style.FILL);
        mMainPaint.setColor(mColor);

        /*mShadowPaint = new Paint(0);
        mShadowPaint.setColor(0xff101010);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));*/
    }
}
