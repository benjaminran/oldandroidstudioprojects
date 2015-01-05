package com.bran.tunerviewexperiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Beni on 10/1/14.
 */
public class TunerView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawingThread drawingThread;

    private int frameCounter;
    private float angle;
    private static final int PERIOD_IN_FRAMES = 10000;

    private Paint paint;

    public TunerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        getHolder().addCallback(this);
        drawingThread = new DrawingThread(getHolder(), this);
        frameCounter = 0;
        paint = new Paint();
        setFocusable(true);
    }

    public void adjustReading(TunerReading reading) {

    }

    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        angle = (float) ((float) 90 * Math.sin((2*Math.PI)/PERIOD_IN_FRAMES * frameCounter));

        int centerX = canvas.getWidth()/2;
        int centerY = canvas.getHeight()/2;
        canvas.drawLine(centerX, centerY, (float) (centerX + Math.cos(angle)), (float) (centerY - Math.sin(angle)), paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawingThread.setKeepDrawing(true);
        drawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while(retry) {
            try {
                drawingThread.join();
                retry = false;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
