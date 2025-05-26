package com.example.project1.trenning;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.ApiClient;
import com.example.project1.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final List<Workout> workouts;
    private OnCategoryClickListener onCategoryClickListener;
    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
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

        // Завантаження зображення через Glide
        Glide.with(holder.itemView.getContext())
                .load(ApiClient.BASE_URL +"/images/" + workout.getPicPath())
                .placeholder(R.drawable.kardio)
                .into(holder.ivWorkout);

        // Обробник натискання на категорію
        holder.itemView.setOnClickListener(v -> {
            Log.e("WorkoutAdapter", "🖱 Натискання на категорію: " + workout.getTitle());
            Log.e("WorkoutAdapter", "onBindViewHolder адаптера: " + this);

            if (onCategoryClickListener != null) {
                Log.e("WorkoutAdapter", "✅ Listener викликано!");
                onCategoryClickListener.onCategoryClick(workout);
            } else {
                Log.e("WorkoutAdapter", "❌ Listener не встановлений!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWorkout;
        TextView tvTitle, tvDuration, tvExercise;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkout = itemView.findViewById(R.id.pic);
            tvTitle = itemView.findViewById(R.id.titleText);
            tvDuration = itemView.findViewById(R.id.durationText);
            tvExercise = itemView.findViewById(R.id.excerciseText);

        }
    }

    // Інтерфейс для обробки кліків
    public interface OnCategoryClickListener {
        void onCategoryClick(Workout workout);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        Log.e("WorkoutAdapter", "Listener встановлено на адаптер: " + this);
        this.onCategoryClickListener = listener;
        Log.e("WorkoutAdapter", "✅ Listener встановлено!");
    }
}
