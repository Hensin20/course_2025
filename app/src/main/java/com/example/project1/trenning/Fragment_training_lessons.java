package com.example.project1.trenning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment_training_lessons extends Fragment {

    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private String workoutId, title, duration, exercise, imagePath;
    private TextView tvTitle, tvDurations, tvExcercise;
    private RecyclerView recyclerView;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Отримання аргументів з попереднього фрагменту
        if (getArguments() != null) {
            workoutId = getArguments().getString("workoutId");
            Log.e("Fragment_training_lessons", "отримано workoutId" + workoutId);
            title = getArguments().getString("title");
            duration = getArguments().getString("duration");
            exercise = getArguments().getString("exercise");
            imagePath = getArguments().getString("imagePath");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trening_lessons, container, false);

        // Ініціалізація елементів UI
        tvTitle = view.findViewById(R.id.titleText);
        tvDurations = view.findViewById(R.id.durationText);
        tvExcercise = view.findViewById(R.id.excerciseText);
        imageView = view.findViewById(R.id.imageView_pic);
        recyclerView = view.findViewById(R.id.list_lesson);

        // Встановлення даних у в'юхи
        tvTitle.setText(title);
        tvDurations.setText(duration);
        tvExcercise.setText(exercise);

        Glide.with(requireContext())
                .load("http://10.0.2.2:5000/images/" + imagePath)
                .placeholder(R.drawable.kardio)
                .into(imageView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseAdapter = new ExerciseAdapter(exerciseList, requireContext());

        recyclerView.setAdapter(exerciseAdapter);

        // Обробка натискання на картинку — приклад переходу (можна змінити логіку)
        exerciseAdapter.setClickListener(exercise -> {
            String videoUrl = exercise.getVideoUrl();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube"); // Пробує відкрити саме YouTube

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    // Якщо YouTube не встановлено — відкриє в браузері
                    Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                    startActivity(fallbackIntent);
                }
            }
        });

        // Завантаження вправ
        fetchExercises();

        return view;
    }

    private void fetchExercises() {


        String url = "http://10.0.2.2:5000/api/workouts/" + workoutId + "/exercises";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (workoutId == null || workoutId.isEmpty()) {
                    Log.e("Fragment_training_lessons", "❌ workoutId порожній або null");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Exercise>>() {}.getType();
                    List<Exercise> loadedExercises = gson.fromJson(json, listType);

                    Log.e("Fragment_training_lessons", "✅ JSON розпарсено успішно!");

                    requireActivity().runOnUiThread(() -> {
                        exerciseList.clear();
                        exerciseList.addAll(loadedExercises);
                        exerciseAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e("Fragment_training_lessons", "❌ Помилка завантаження вправ");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Fragment_training_lessons", "❌ Запит не вдався: " + e.getMessage());
            }
        });
    }
}
