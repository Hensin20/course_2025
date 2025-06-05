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
import java.util.List;

public class SetupChart {

    public static void setupChart(BarChart barChart, Context context, List<UserStatsModel> stats) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        int Green_good = ContextCompat.getColor(context, R.color.Green_good);
        int Blue_nord = ContextCompat.getColor(context, R.color.Blue_norm);
        int Gold_record = ContextCompat.getColor(context, R.color.Gold_record);
        int background = ContextCompat.getColor(context, R.color.fragment);
        int textColors = ContextCompat.getColor(context, R.color.text_code);

        String[] days = new String[]{"П", "В", "С", "Ч", "П", "С", "Н"};

        for (int i = 0; i < stats.size(); i++) {
            int steps = stats.get(i).getSteps();
            entries.add(new BarEntry(i, steps));

            if (steps >= 10000) {
                colors.add(Gold_record);
            } else if (steps <= 6000) {
                colors.add(Blue_nord);
            } else {
                colors.add(Green_good);
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Тиждень");
        dataSet.setColors(colors);

        // ✅ Додано відображення значень кроків
        dataSet.setValueTextSize(12f); // Розмір тексту
        dataSet.setValueTextColor(textColors); // Колір тексту

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);

        barChart.setDrawGridBackground(true);
        barChart.setGridBackgroundColor(background);
        barChart.setDrawBorders(true);
        barChart.setBorderColor(background);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < days.length) ? days[index] : "";
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(textColors);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().enableGridDashedLine(10f, 10f, 0f);
        barChart.getAxisLeft().setTextSize(12f);
        barChart.getAxisLeft().setTextColor(textColors);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
