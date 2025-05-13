package com.example.project1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project1.Domain.FetchWorkouts;
import com.example.project1.Domain.Workout;
import com.example.project1.Domain.WorkoutAdapter;

import java.util.ArrayList;
import java.util.List;

public class Fragment_training extends Fragment {

    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private List<Workout> workouts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_training, container, false);

        // Ініціалізація RecyclerView
        recyclerView = view.findViewById(R.id.list_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter(adapter);

        Log.e("Fragment_training", "✅ Adapter створений, додаємо listener!");

        // Додаємо обробку натисканняloadWorkouts
        adapter.setOnCategoryClickListener(workout -> {
            Log.e("Fragment_training", "✅ Клік по категорії: " + workout.getTitle());

            Fragment_training_lessons fragment = new Fragment_training_lessons();
            Bundle bundle = new Bundle();
            bundle.putString("workoutId", workout.getWorkoutId());
            Log.e("Fragment_training", "Передано workoutId" + workout.getWorkoutId());
            bundle.putString("title", workout.getTitle());
            bundle.putString("duration", workout.getDurationAll());
            bundle.putString("exercise", workout.getExercise());
            bundle.putString("imagePath", workout.getPicPath());
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();

            Log.e("Fragment_training", "🔥 Перехід виконано!");
        });

        // Завантаження даних
        loadWorkouts();

        return view;
    }

    private void loadWorkouts() {
        Log.e("loadWorkouts", "Метод запущено!");

        if (getContext() == null) {
            Log.e("loadWorkouts", "getContext() == null, вихід!");
            return;
        }

        if (recyclerView == null) {
            Log.e("loadWorkouts", "RecyclerView не ініціалізовано!");
            return;
        }

        FetchWorkouts fetchWorkouts = new FetchWorkouts(getContext()) {
            @Override
            protected void onDataLoaded(List<Workout> data) {
                workouts.clear();
                workouts.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            protected void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        };
        fetchWorkouts.fetchWorkouts();

        Log.e("loadWorkouts", "📌 Виклик fetchWorkouts.fetchWorkouts()");
        fetchWorkouts.fetchWorkouts();
    }
}
