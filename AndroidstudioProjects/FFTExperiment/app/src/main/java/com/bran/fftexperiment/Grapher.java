package com.bran.fftexperiment;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

/**
 * Created by Beni on 8/28/14.
 */
public class Grapher {
    private Context context;
    private LinearLayout outputContainer;
    private LinearLayout.LayoutParams layoutParams;

    MainActivity mainActivity;

    public Grapher(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        outputContainer = (LinearLayout) mainActivity.findViewById(R.id.outputcontainer);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        //layoutParams.topMargin = 10;

    }

    public void graph(double[] data, String heading, Boolean isInputGraph) {
        graph(data, heading, isInputGraph, 1.0);
    }

    public void graph(double[] data, String heading, Boolean isInputGraph, double xFactor) {
        data[2] = 2.0;
        GraphView.GraphViewData[] graphData = new GraphView.GraphViewData[data.length];
        for(int i=0; i<data.length; i++) {
            graphData[i] = new GraphView.GraphViewData(i*xFactor, i);//data[i]);
        }
        GraphViewSeries dataSeries = new GraphViewSeries(graphData);

        GraphView graphView = new LineGraphView(context, heading);
        graphView.addSeries(dataSeries); // data
        graphView.setScalable(true);

        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundColor(Color.CYAN);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);

        /*ViewGroup.LayoutParams LLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        LL.setWeightSum(6f);
        LL.setLayoutParams(LLParams);*/



        if(isInputGraph) {
            LinearLayout l = (LinearLayout) mainActivity.findViewById(R.id.input);
            l.addView(graphView);
        }
        else {
            TextView tv = new TextView(context);
            tv.setText("Dynamic Text!");
            layout.addView(tv);//graphView);
            layout.addView(graphView);
            outputContainer.addView(layout);
        }
    }
}
