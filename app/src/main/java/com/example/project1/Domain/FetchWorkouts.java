package com.example.project1.Domain;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchWorkouts {
    private Context context;
    private RecyclerView recyclerView;

    // Конструктор, що приймає контекст і RecyclerView
    public FetchWorkouts(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void fetchWorkouts() {
        // 1. Створюємо запит
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/workouts")
                .get()
                .build();

        // 2. Виконуємо асинхронний запит
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    List<Workout> workouts = parseWorkouts(json);

                    // Оновлюємо UI у головному потоці
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity)context).runOnUiThread(() -> setupRecyclerView(workouts));
                    } else {
                        Log.e("FetchWorkouts", "⚠️ Отриманий контекст: " + context.getClass().getSimpleName());
                    }
                } else {
                    ((MainActivity)context).runOnUiThread(() ->
                            Toast.makeText(context, "Помилка: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ((MainActivity)context).runOnUiThread(() ->
                        Toast.makeText(context, "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private List<Workout> parseWorkouts(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Workout>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    private void setupRecyclerView(List<Workout> workouts) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        WorkoutAdapter adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter(adapter);
    }
}