package com.bran.autocorellationexperiment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

/**
 * Created by Beni on 9/11/14.
 */
public class Grapher implements View.OnClickListener {
    private Context context;
    private Activity mainActivity;

    private LinearLayout.LayoutParams layoutParams;
    LinearLayout graphContainer;

    public Grapher(Context context, Activity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        graphContainer = (LinearLayout) mainActivity.findViewById(R.id.graph_container);
    }

    public void drawGraph(double[] data, String title, Boolean isOutputGraph) {
        GraphView.GraphViewData[] graphViewData = new GraphView.GraphViewData[data.length];
        for(int i=0;i<data.length;i++) graphViewData[i] = new GraphView.GraphViewData(i, data[i]);
        GraphViewSeries exampleSeries = new GraphViewSeries(graphViewData);
        LineGraphView graphView = new LineGraphView(context, title);
//        GraphView graphView = isOutputGraph ? new BarGraphView(context, title) : new LineGraphView(context, title);
        GraphViewSeries xAxis = new GraphViewSeries("X axis", new GraphViewSeries.GraphViewSeriesStyle(Color.BLACK, 3), new GraphView.GraphViewData[]{
                new GraphView.GraphViewData(0,0), new GraphView.GraphViewData(graphViewData[data.length-1].getX(), 0) });
        graphView.addSeries(xAxis);
        graphView.addSeries(exampleSeries);
        graphView.setShowLegend(false);
        graphView.setOnClickListener(this);
        if(isOutputGraph) {
            //graphView.setViewPort(140, 600);
            //graphView.setScrollable(true);
            //graphView.setDrawDataPoints(true);
            //graphView.setDataPointsRadius(15f);
        }
        graphView.setScalable(true);
        graphContainer.addView(graphView, layoutParams);
        Log.i("fle", "Graph added.");
    }

    public void drawText(String text) {
        ScrollView scrollView = new ScrollView(context);
        TextView textView = new TextView(context);
        textView.setText(Html.fromHtml(text));
        scrollView.addView(textView);
        graphContainer.addView(scrollView, layoutParams);
    }

    @Override
    public void onClick(View v) { // Parameter v stands for the view that was clicked.
        ((LinearLayout) v.getParent()).removeView(v);
        Log.i("fle", "Graph removed.");
    }
}
