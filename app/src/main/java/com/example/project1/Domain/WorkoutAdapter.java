// WorkoutAdapter.java
package com.example.project1.Domain;  // Створіть папку Adapters у вашому пакеті

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Domain.Workout;
import com.example.project1.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final List<Workout> workouts;

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

        // Завантаження зображення через Glide
        Glide.with(holder.itemView.getContext())
                .load("http://10.0.2.2:5000/images/" + workout.getPicPath())
                .placeholder(R.drawable.kardio)
                .into(holder.ivWorkout);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWorkout;
        TextView tvTitle, tvDuration;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkout = itemView.findViewById(R.id.pic);
            tvTitle = itemView.findViewById(R.id.durationText);
            tvDuration = itemView.findViewById(R.id.excerciseText);
        }
    }
}