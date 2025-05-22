package com.example.project1.trenning;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class FetchWorkouts {
    private final WeakReference<Context> contextRef;
    private Call currentCall;

    public FetchWorkouts(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    public void fetchWorkouts() {
        if (currentCall != null) {
            currentCall.cancel(); // Скасувати попередній запит
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/workouts")
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        currentCall = client.newCall(request);
        currentCall.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Context context = contextRef.get();
                if (context == null) return;

                try {
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
                } catch (Exception e) {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity)context).runOnUiThread(() ->
                                onError("Помилка обробки: " + e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Context context = contextRef.get();
                if (context instanceof AppCompatActivity && !call.isCanceled()) {
                    ((AppCompatActivity)context).runOnUiThread(() ->
                            onError("Мережева помилка: " + e.getMessage()));
                }
            }
        });
    }

    public void cancel() {
        if (currentCall != null) {
            currentCall.cancel();
        }
    }

    private List<Workout> parseWorkouts(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Workout>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    protected abstract void onDataLoaded(List<Workout> workouts);
    protected abstract void onError(String errorMessage);
}
