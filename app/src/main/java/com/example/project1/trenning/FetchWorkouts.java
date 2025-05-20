package com.example.project1.trenning;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public abstract class FetchWorkouts {
    private Context context;

    public FetchWorkouts(Context context) {
        this.context = context;
    }

    public void fetchWorkouts() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/workouts")
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    List<Workout> workouts = parseWorkouts(json);

                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity)context).runOnUiThread(() -> onDataLoaded(workouts));
                    }
                } else {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity)context).runOnUiThread(() ->
                                onError("Помилка: " + response.code()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity)context).runOnUiThread(() ->
                            onError("Помилка: " + e.getMessage()));
                }
            }
        });
    }

    private List<Workout> parseWorkouts(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Workout>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    protected abstract void onDataLoaded(List<Workout> workouts);

    protected abstract void onError(String errorMessage);
}
