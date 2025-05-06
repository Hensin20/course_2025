package com.example.project1;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class SetupChart  {

    public static void setupChart(BarChart barChart, Context context, int[] stepsPerDay) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();


        // colors
        int Green_good = ContextCompat.getColor(context,R.color.Green_good);
        int Blue_nord = ContextCompat.getColor(context,R.color.Blue_norm);
        int Gold_record = ContextCompat.getColor(context,R.color.Gold_record);

// Підписи осі X
        String[] days = new String[]{"П", "В", "С", "Ч", "П", "С", "Н"};

        for(int i=0;i<stepsPerDay.length;i++){
            entries.add(new BarEntry(i,stepsPerDay[i]));

            if (stepsPerDay[i] >= 10000) {
                colors.add(Gold_record);

            } else if (stepsPerDay[i] <= 6000) {
                colors.add(Blue_nord);

            } else {
                colors.add(Green_good);
            }
        }
        BarDataSet dataSet = new BarDataSet(entries, "Тиждень");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false); // не показувати значення зверху

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);

// Стилизація графіка
        barChart.setDrawGridBackground(true);
        barChart.setGridBackgroundColor(Color.parseColor("#E3F2FD")); // світло-блакитний фон
        barChart.setDrawBorders(true);
        barChart.setBorderColor(Color.parseColor("#90CAF9")); // рамка

// Осі
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < days.length) {
                    return days[index];
                } else {
                    return "";
                }
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);

        barChart.getAxisRight().setEnabled(false);

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().enableGridDashedLine(10f, 10f, 0f); // пунктир
        barChart.getAxisLeft().setTextSize(12f);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false); // сховати легенду
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
