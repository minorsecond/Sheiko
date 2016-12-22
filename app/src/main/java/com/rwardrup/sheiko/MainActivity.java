package com.rwardrup.sheiko;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
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

        // Add the points to the line graph
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                //new DataPoint(0, 1),
                //new DataPoint(1, 5),
                //new DataPoint(2, 3),
                //new DataPoint(3, 2),
                //new DataPoint(4, 6)

                new DataPoint(d1, 25475),
                new DataPoint(d2, 22330),
                new DataPoint(d3, 28795),
                new DataPoint(d4, 29977),
                new DataPoint(d5, 22590),
                new DataPoint(d6, 22550)
        });
        graph.addSeries(series);

        // Set the date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        // Label the y-axis. TODO: let this be set by the user in the settings window.
        graph.getGridLabelRenderer().setVerticalAxisTitle("Total Lbs.");

        // set manual x bounds to have nice steps
        //graph.getViewport().setMinX(d1.getTime());
        //graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

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
