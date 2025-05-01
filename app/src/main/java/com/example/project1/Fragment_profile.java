package com.example.project1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;

public class Fragment_profile extends Fragment implements SensorEventListener {

    private BarChart barChart;
    private SensorManager sensorManager = null;
    private Sensor stepSensor;
    private int totalStep = 0;
    private int praviewsTotalStep = 0;
    private TextView textView_steps, textView_calories, textView_distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmen_profile, container, false);

        barChart = view.findViewById(R.id.barChart);

        textView_steps = view.findViewById(R.id.textView_steps_count);
        textView_calories = view.findViewById(R.id.textView_calories_count);
        textView_distance = view.findViewById(R.id.textView_distance_count);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);



        setupChart(stepsPerDay); // Створюємо графік

        return view;
    }



    int[] stepsPerDay = {5000, 7000, 4000, 8000, 6000, 3000, 10000};
    private void setupChart(int [] stepsPerDay) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

 // colors
        int Green_good = ContextCompat.getColor(requireContext(),R.color.Green_good);
        int Blue_nord = ContextCompat.getColor(requireContext(),R.color.Blue_norm);
        int Gold_record = ContextCompat.getColor(requireContext(),R.color.Gold_record);

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


    public void onResume() {
        super.onResume();
        if(stepSensor == null){
            Toast.makeText(requireContext(), "Ваш пристрій не підтримує лічильник кроків", Toast.LENGTH_LONG).show();
        }
        else{
            sensorManager.registerListener(this,stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (praviewsTotalStep == 0) {
                praviewsTotalStep = (int) event.values[0];
                // Зберегти в SharedPreferences, якщо потрібно
            }

            int currentSteps = (int) event.values[0] - praviewsTotalStep;
            textView_steps.setText(String.valueOf(currentSteps));

            // Додатково можна порахувати калорії та відстань:
            float calories = currentSteps * 0.04f; // приблизно
            float distance = currentSteps * 0.7f / 1000f; // 0.7 метра на крок, у км

            textView_calories.setText(String.format("%.1f ккал", calories));
            textView_distance.setText(String.format("%.2f км", distance));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("stepPrefs", Context.MODE_PRIVATE);
        praviewsTotalStep = prefs.getInt("prevSteps", 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        SharedPreferences prefs = requireContext().getSharedPreferences("stepPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("prevSteps", praviewsTotalStep);
        editor.apply();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Це можна залишити пустим, якщо не потрібно
    }
}
