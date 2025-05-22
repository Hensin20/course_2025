package com.example.project1;

import static android.app.ProgressDialog.show;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class Fragment_profile extends Fragment implements SensorEventListener {

    private BarChart barChart;
    private SensorManager sensorManager = null;
    private Sensor stepSensor;
    private int praviewsTotalStep = 0;
    private TextView textView_steps, textView_calories, textView_distance;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        barChart = view.findViewById(R.id.barChart);

        textView_steps = view.findViewById(R.id.textView_steps_count);
        textView_calories = view.findViewById(R.id.textView_calories_count);
        textView_distance = view.findViewById(R.id.textView_distance_count);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        int[] stepsPerDay = {5000, 7000, 4000, 8000, 6000, 3000, 10000};

        SetupChart.setupChart(barChart,requireContext(),stepsPerDay);

        SharedPreferences prefs = getActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if(userId != -1){
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        }

        return view;
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
        SharedPreferences prefs = requireContext().getSharedPreferences("stepPrefs", MODE_PRIVATE);
        praviewsTotalStep = prefs.getInt("prevSteps", 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        SharedPreferences prefs = requireContext().getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("prevSteps", praviewsTotalStep);
        editor.apply();

        // --- ВИКЛИК ЗАПИСУ ---
        SharedPreferences userPrefs = requireContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("userId", -1);

        if (userId != -1) {
            int steps = Integer.parseInt(textView_steps.getText().toString());
            float calories = steps * 0.04f;
            float distance = steps * 0.7f / 1000f;
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            uploadStats(userId, steps, calories, distance, currentDate);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Це можна залишити пустим, якщо не потрібно
    }

    private void uploadStats(int userId, int steps, float calories, float distanceKm, String date) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("steps", steps);
            json.put("calories", calories);
            json.put("distanceKm", distanceKm);
            json.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/auth/stats/upload")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (isAdded()) { // <--- Перевірка, чи фрагмент прикріплений
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Помилка при надсиланні статистики", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                if (isAdded()) { // <--- Перевірка, чи фрагмент прикріплений
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), responseText, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

}
