package com.example.project1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Callback;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendStatsToServer {

    private void sendStatsToServer(int userId, int steps, float calories, float distanceKm) {
        OkHttpClient client = new OkHttpClient();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        StatsModel stats = new StatsModel(userId, steps, calories, distanceKm, date);

        Gson gson = new Gson();

        String json = gson.toJson(stats);  // Конвертація Java-об'єкта в JSON

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/auth/upload")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("STATS", "Не вдалося відправити: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("STATS", "Успішно збережено статистику");
                } else {
                    Log.e("STATS", "Помилка: " + response.code());
                }
            }
        });
    }
    public class StatsModel {
        public int userId;
        public int steps;
        public float calories;
        public float distanceKm;
        public String date;

        public StatsModel(int userId, int steps, float calories, float distanceKm, String date) {
            this.userId = userId;
            this.steps = steps;
            this.calories = calories;
            this.distanceKm = distanceKm;
            this.date = date;
        }
    }
}
