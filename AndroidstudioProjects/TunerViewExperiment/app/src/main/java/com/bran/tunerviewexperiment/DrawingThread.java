package com.bran.tunerviewexperiment;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Beni on 10/1/14.
 */
public class DrawingThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private TunerView tunerView;
    private Canvas canvas;

    private Boolean keepDrawing;

    private int x;

    public DrawingThread(SurfaceHolder surfaceHolder, TunerView tunerView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.tunerView = tunerView;
        keepDrawing = false;
    }

    @Override
    public void run() {
        while(keepDrawing) {
            canvas = surfaceHolder.lockCanvas();
            if(canvas!=null) {
                tunerView.doDraw(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            else Log.i("tve", "Canvas is null!");
        }
    }

    public void setKeepDrawing(Boolean keepDrawing) {
        this.keepDrawing = keepDrawing;
    }
}
