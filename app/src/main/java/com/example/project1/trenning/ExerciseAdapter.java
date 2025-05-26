package com.example.project1.trenning;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.ApiClient;
import com.example.project1.R;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private final List<Exercise> exercises;
    private final Context context;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    private OnExerciseClickListener clickListener;

    public ExerciseAdapter(List<Exercise> exercises, Context context) {
        this.exercises = exercises;
        this.context = context;
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
        Exercise exercise = exercises.get(position);
        holder.title.setText(exercise.getTitle());
        holder.duration.setText(exercise.getDurationSeconds());

        // Перевіряємо на null або пустоту перед завантаженням зображення
        if (exercise.getPreviewImageUrl() != null && !exercise.getPreviewImageUrl().isEmpty()) {
            Log.e("ExerciseAdapter", "✅ Отримано PreviewImageUrl: " + exercise.getPreviewImageUrl());
            Glide.with(context)
                    .load(ApiClient.BASE_URL +"/images/" + exercise.getPreviewImageUrl()) // Додай абсолютний шлях
                    .placeholder(R.drawable.kardio)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.kardio);
        }

        holder.image_play.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExerciseClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView image, image_play;
        TextView title, duration;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.pic);
            title = itemView.findViewById(R.id.titleText);
            duration = itemView.findViewById(R.id.durationText);
            image_play = itemView.findViewById(R.id.imageView_play);
        }
    }

}


