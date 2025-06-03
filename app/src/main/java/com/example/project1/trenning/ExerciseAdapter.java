package com.example.project1.trenning;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.ApiClient;
import com.example.project1.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private final List<Exercise> exercises;
    private final Context context;
    private final Fragment_training_lessons fragmentTrainingLessons; // ✅ Додаємо фрагмент
    private final OkHttpClient client = new OkHttpClient();



    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    private OnExerciseClickListener clickListener;

    public ExerciseAdapter(List<Exercise> exercises, Context context, Fragment_training_lessons fragmentTrainingLessons) {
        this.exercises = exercises;
        this.context = context;
        this.fragmentTrainingLessons = fragmentTrainingLessons; // ✅ Передаємо фрагмент
    }


    public void setClickListener(OnExerciseClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vlewholder_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {

        SharedPreferences prefs = context.getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userRole = prefs.getString("userRole", "user"); // ✅ Отримуємо роль користувача

        if (!userRole.equals("admin")) {
            holder.imageViewTrash.setVisibility(View.GONE);
            holder.imageView_edit.setVisibility(View.GONE);
        } else {
            holder.imageViewTrash.setVisibility(View.VISIBLE);
            holder.imageView_edit.setVisibility(View.VISIBLE);
        }

        Exercise exercise = exercises.get(position);
        holder.title.setText(exercise.getTitle());
        holder.duration.setText(exercise.getDurationSeconds());

        // ✅ Відображення зображення
        if (exercise.getPreviewImageUrl() != null && !exercise.getPreviewImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(ApiClient.BASE_URL + "/images/" + exercise.getPreviewImageUrl())
                    .placeholder(R.drawable.kardio)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.kardio);
        }

        // ✅ Видалення вправи
        holder.imageViewTrash.setOnClickListener(v -> {
            deleteExercise(String.valueOf(exercise.getExerciseId()), position);
        });

        // ✅ Редагування вправи
        holder.imageView_edit.setOnClickListener(v -> {
            fragmentTrainingLessons.showAddExerciseDialog(exercise); // ✅ Викликаємо метод фрагмента
        });

    }



    private void deleteExercise(String exerciseId, int position) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String json = "{\"exerciseId\":" + exerciseId + "}"; // ✅ Тепер це число, а не рядок

        String url = ApiClient.BASE_URL + "/api/workouts/deleteExercise/" + exerciseId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ((Activity) context).runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "✅ Вправа видалена!", Toast.LENGTH_SHORT).show();
                        exercises.remove(position); // Видаляємо вправу з локального списку
                        notifyItemRemoved(position);
                    } else {
                        Log.d("DELETE_EXERCISE", "Запит JSON: " + json);
                        Log.d("DELETE_EXERCISE", "URL: " + ApiClient.BASE_URL + "/api/workouts/deleteExercise");

                        Log.d("DELETE_EXERCISE", "Exercise ID: " + exerciseId );

                        Toast.makeText(context, "❌ Помилка видалення!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "⚠ Помилка мережі!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView image, image_play, imageViewTrash, imageView_edit;
        TextView title, duration;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.pic);
            title = itemView.findViewById(R.id.titleText);
            duration = itemView.findViewById(R.id.durationText);
            image_play = itemView.findViewById(R.id.imageView_play);
            imageViewTrash = itemView.findViewById(R.id.imageView_trash);
            imageView_edit = itemView.findViewById(R.id.imageView_edit);

        }
    }
}
