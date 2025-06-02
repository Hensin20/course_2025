package com.example.project1.trenning;

import android.app.Activity;
import android.content.Context;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final List<Workout> workouts;
    private final Context context;
    private final OkHttpClient client = new OkHttpClient(); // ✅ Додано OkHttpClient
    private OnCategoryClickListener onCategoryClickListener;

    public WorkoutAdapter(List<Workout> workouts, Context context) {
        this.workouts = workouts;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vlewholder_worktout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.tvTitle.setText(workout.getTitle());
        holder.tvDuration.setText(workout.getDurationAll());
        holder.tvExercise.setText(workout.getExercise());

        // ✅ Завантаження зображення через Glide
        Glide.with(holder.itemView.getContext())
                .load(ApiClient.BASE_URL + "/images/" + workout.getPicPath())
                .placeholder(R.drawable.kardio)
                .into(holder.ivWorkout);

        // ✅ Обробник натискання на категорію
        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(workout);
            }
        });

        // ✅ Видалення категорії
        holder.ivDeleteCategory.setOnClickListener(v -> {
            deleteCategory(Integer.parseInt(workout.getWorkoutId()), position); // ✅ Перетворюємо рядок у число

        });
    }

    private void deleteCategory(int workoutId, int position) {
        Request request = new Request.Builder()
                .url(ApiClient.BASE_URL + "/api/workouts/deleteCategory?workoutId=" + workoutId) // ✅ Передаємо ID в URL
                .delete()
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ((Activity) context).runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "✅ Категорію видалено!", Toast.LENGTH_SHORT).show();
                        workouts.remove(position); // ✅ Видаляємо локально
                        notifyItemRemoved(position);
                    } else {
                        Toast.makeText(context, "❌ Помилка видалення!", Toast.LENGTH_SHORT).show();
                        Log.e("DELETE_CATEGORY", "❌ Код відповіді сервера: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "⚠ Помилка мережі!", Toast.LENGTH_SHORT).show());
                Log.e("DELETE_CATEGORY", "❌ Мережева помилка: " + e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWorkout, ivDeleteCategory;
        TextView tvTitle, tvDuration, tvExercise;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkout = itemView.findViewById(R.id.pic);
            tvTitle = itemView.findViewById(R.id.titleText);
            tvDuration = itemView.findViewById(R.id.durationText);
            tvExercise = itemView.findViewById(R.id.excerciseText);
            ivDeleteCategory = itemView.findViewById(R.id.imageView_trash); // ✅ Виправлено ID кнопки
        }
    }

    // ✅ Інтерфейс для кліків
    public interface OnCategoryClickListener {
        void onCategoryClick(Workout workout);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }
}
