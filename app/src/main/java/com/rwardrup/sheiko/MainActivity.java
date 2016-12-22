package com.rwardrup.sheiko;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.util.Date;

//TODO: Purchase this icon: http://www.flaticon.com/free-icon/bench_249226#term=weightlifting&page=1&position=24

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The graph on the main window. See www.android-graphview.org for more info
        Calendar calendar = Calendar.getInstance();

        // Add example dates
        String date1 = "01/21/2016";
        String date2 = "03/21/2016";
        String date3 = "05/21/2016";
        String date4 = "07/21/2016";
        String date5 = "09/21/2016";
        String date6 = "11/21/2016";

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        Date d1 = null;
        Date d2 = null;
        Date d3 = null;
        Date d4 = null;
        Date d5 = null;
        Date d6 = null;

        // Create Date objects from the strings declared above.
        // For the app, this will probably need to be done using a for loop.

        try {
            d1 = df.parse(date1);
            calendar.add(Calendar.DATE, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            d2 = df.parse(date2);
            calendar.add(Calendar.DATE, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            d3 = df.parse(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            d4 = df.parse(date4);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            d5 = df.parse(date5);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            d6 = df.parse(date6);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Add the points to the line graph - Not currently used
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> totalWeightLifted = new LineGraphSeries<>(new DataPoint[]{

                new DataPoint(d1, 25475),
                new DataPoint(d2, 22330),
                new DataPoint(d3, 28795),
                new DataPoint(d4, 29977),
                new DataPoint(d5, 22590),
                new DataPoint(d6, 22550)
        });

        // Series style
        totalWeightLifted.setTitle("Volume");
        totalWeightLifted.setColor(Color.rgb(27, 94, 32));
        totalWeightLifted.setDrawDataPoints(true);
        totalWeightLifted.setDataPointsRadius(10);
        totalWeightLifted.setThickness(8);

        LineGraphSeries<DataPoint> numberLifts = new LineGraphSeries<>(new DataPoint[]{

                new DataPoint(d1, 250),
                new DataPoint(d2, 220),
                new DataPoint(d3, 275),
                new DataPoint(d4, 325),
                new DataPoint(d5, 230),
                new DataPoint(d6, 210)
        });

        // Series style
        numberLifts.setTitle("# Lifts");
        numberLifts.setColor(Color.rgb(27, 94, 32));
        numberLifts.setDrawDataPoints(true);
        numberLifts.setDataPointsRadius(10);
        numberLifts.setThickness(8);

        LineGraphSeries<DataPoint> averageWeightLifted = new LineGraphSeries<>(new DataPoint[]{

                new DataPoint(d1, 350),
                new DataPoint(d2, 375),
                new DataPoint(d3, 315),
                new DataPoint(d4, 335),
                new DataPoint(d5, 375),
                new DataPoint(d6, 395)
        });

        // Series style
        averageWeightLifted.setTitle("Average Wt. Lifted");
        averageWeightLifted.setColor(Color.rgb(76, 175, 80));
        averageWeightLifted.setDrawDataPoints(true);
        averageWeightLifted.setDataPointsRadius(10);
        averageWeightLifted.setThickness(8);

        //graph.addSeries(totalWeightLifted);
        graph.addSeries(numberLifts);
        graph.getSecondScale().addSeries(averageWeightLifted);

        // Set the date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.rgb(27, 94, 32));

        // Label the y-axis. TODO: let this be set by the user in the settings window.
        graph.getGridLabelRenderer().setVerticalAxisTitle("Lbs.");

        // set manual x bounds to have nice steps
        //graph.getViewport().setMinX(d1.getTime());
        //graph.getViewport().setMaxX(d6.getTime());
        //graph.getViewport().setXAxisBoundsManual(true);

        // User scrollable x axis
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        //graph.getViewport().setScalableY(true);
        //graph.getViewport().setScrollableY(true);

        // Set second scale bounds
        graph.getSecondScale().setMinY(150);
        graph.getSecondScale().setMaxY(450);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.rgb(76, 175, 80));
        graph.getGridLabelRenderer().setVerticalAxisTitle("# Lifts");

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        // Set label font size
        graph.getGridLabelRenderer().setTextSize(26f);
        graph.getGridLabelRenderer().reloadStyles();

        // Enable legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setBackgroundColor(Color.argb(200, 232, 245, 233));

        // set Title
        graph.setTitle("Program History");

        // Handle button clicks that take user to another action.

        //Settings button -> settings action
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
            }
        });

        // Select Program button -> select program action
        Button selectProgramButton = (Button) findViewById(R.id.selectProgramButton);
        selectProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, selectProgram.class));
            }
        });
    }
}
