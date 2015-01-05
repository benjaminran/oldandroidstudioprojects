package com.bran.audiorecordexperiment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;

/**
 * Created by Beni on 7/29/14.
 */
public class FilterGrapher implements DataFilter {
    private static final String TAG = "ARE";
    private static final int GRAPH_DATA_SIZE = 2000;
    private ArrayList<GraphView.GraphViewData> graphData;
    private int graphCounter;
    private GraphViewSeries dataSeries;
    private GraphView graphView;

    private Context context;
    private MainActivity mainActivity;

    public FilterGrapher(Context context,MainActivity mainActivity) {
        // init graph UI
        graphData = new ArrayList<GraphView.GraphViewData>(GRAPH_DATA_SIZE);
        graphCounter = 0;
        for(int i=0; i<GRAPH_DATA_SIZE; i++) {
            graphData.add(new GraphView.GraphViewData(i, 0.0));
            graphCounter++;
        }

        this.context = context;
        this.mainActivity = mainActivity;
    }

    public void processBuffer(short[] buffer, int read) {
        for(int i=0; i< read; i++) {
            graphData.remove(0);
            graphData.add(new GraphView.GraphViewData(graphCounter, buffer[i]));
            graphCounter++;
        }
    }

    public void refreshData(){
        if(graphView!=null) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GraphView.GraphViewData[] data = graphData.toArray(new GraphView.GraphViewData[GRAPH_DATA_SIZE]);
                    for(int i=1; i<data.length; i++) {
                        if(data[i].getX() < data[i-1].getX()) Log.e(TAG, "Error!");
                    }
                    dataSeries.resetData(data);
                    LinearLayout layout = (LinearLayout) mainActivity.findViewById(R.id.layout);
                    GraphView graphView = (GraphView) layout.getChildAt(0);
                    graphView.setViewPort(graphData.get(0).getX(), GRAPH_DATA_SIZE);
                }
            });
        }
        else drawGraph();
    }

    public void drawGraph() {
        GraphView.GraphViewData[] data = graphData.toArray(new GraphView.GraphViewData[GRAPH_DATA_SIZE]);
        for(int i=1; i<data.length; i++) {
            if(data[i].getX() < data[i-1].getX()) Log.e(TAG, "Error!");
        }
        dataSeries = new GraphViewSeries(data);
        graphView = new LineGraphView(context, "PCM Data");
        graphView.addSeries(dataSeries);
        graphView.setViewPort(graphData.get(0).getX(), GRAPH_DATA_SIZE);
        graphView.setScrollable(true);
        graphView.setScalable(true);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout = (LinearLayout) mainActivity.findViewById(R.id.layout);
                layout.addView(graphView);
            }
        });
    }

    private void stopGraphing() {
        //TODO
    }
}
