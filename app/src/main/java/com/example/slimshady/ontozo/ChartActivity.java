package com.example.slimshady.ontozo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    private ArrayList<Double> humidityArray = new ArrayList<>();
    private ArrayList<Double> temperatureArray = new ArrayList<>();
    private ArrayList<Double> lightArray = new ArrayList<>();
    private ArrayList<String> timeArray = new ArrayList<>();

    private LineChart chart;
    private TextView tvCurrent;

    private Boolean enabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent intent = getIntent();
        humidityArray = (ArrayList<Double>) intent.getSerializableExtra("hum");
        temperatureArray = (ArrayList<Double>) intent.getSerializableExtra("temp");
        lightArray = (ArrayList<Double>) intent.getSerializableExtra("light");
        timeArray = (ArrayList<String>) intent.getSerializableExtra("time");
        tvCurrent = (TextView)findViewById(R.id.tv_current);
        chart = (LineChart) findViewById(R.id.chart);

        LineData chartData = new LineData(timeArray, getDataSet(humidityArray,temperatureArray,lightArray));
        chart.setData(chartData);
        chart.setDescription("Mote");
        chart.animateXY(2000, 2000);
        chart.invalidate();
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setStartAtZero(false);
        chart.getAxisLeft().setStartAtZero(false);

        chart.setData(chartData);
        chart.invalidate();
        tvCurrent.setText("Hőmérséklet: " + temperatureArray.get(temperatureArray.size()-1) + " Nedvesség: " + humidityArray.get(humidityArray.size()-1) + " Fény: " + lightArray.get(lightArray.size()-1));

    }

    private ArrayList<LineDataSet> getDataSet(ArrayList<Double> values1,ArrayList<Double> values2,ArrayList<Double> values3) {
        ArrayList<LineDataSet> dataSets = null;
        dataSets = new ArrayList<>();

        if(values1 != null && !values1.isEmpty()){
            ArrayList<Entry> valueSet1 = new ArrayList<>();
            for(int i = 0;i<values1.size();i++) {
                valueSet1.add(new Entry(values1.get(i).intValue(), i));
            }
            LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "Humidity");
            lineDataSet1.setColor(Color.rgb(255, 153, 51));
            lineDataSet1.setValueTextColor(Color.BLACK);
            lineDataSet1.setDrawCircles(false);
            dataSets.add(lineDataSet1);
        }
        if(values2 != null && !values2.isEmpty()) {
            ArrayList<Entry> valueSet2 = new ArrayList<>();
            for (int i = 0; i < values2.size(); i++) {
                valueSet2.add(new Entry(values2.get(i).intValue(), i));
            }
            LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "Temperature");
            lineDataSet2.setColor(Color.rgb(255, 255, 51));
            lineDataSet2.setValueTextColor(Color.BLACK);
            lineDataSet2.setDrawCircles(false);
            dataSets.add(lineDataSet2);
        }
        if(values3 != null && !values3.isEmpty()) {
            ArrayList<Entry> valueSet3 = new ArrayList<>();
            for (int i = 0; i < values3.size(); i++) {
                valueSet3.add(new Entry(values3.get(i).intValue(), i));
            }
            LineDataSet lineDataSet3 = new LineDataSet(valueSet3, "Light");
            lineDataSet3.setColor(Color.rgb(153, 255, 51));
            lineDataSet3.setValueTextColor(Color.BLACK);
            lineDataSet3.setDrawCircles(false);
            dataSets.add(lineDataSet3);
        }
        return dataSets;
    }
}
