package com.example.project1.trenning;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project1.R;

import java.util.ArrayList;
import java.util.List;

public class Fragment_training extends Fragment {
    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private List<Workout> workouts = new ArrayList<>();
    private FetchWorkouts fetchWorkouts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        recyclerView = view.findViewById(R.id.list_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter(adapter);

        adapter.setOnCategoryClickListener(workout -> {
            if (isAdded()) { // Перевірка, чи фрагмент доданий до Activity
                Fragment_training_lessons fragment = new Fragment_training_lessons();
                Bundle bundle = new Bundle();
                bundle.putString("workoutId", workout.getWorkoutId());
                bundle.putString("title", workout.getTitle());
                bundle.putString("duration", workout.getDurationAll());
                bundle.putString("exercise", workout.getExercise());
                bundle.putString("imagePath", workout.getPicPath());
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadWorkouts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fetchWorkouts != null) {
            fetchWorkouts.cancel();
        }
    }

    private void loadWorkouts() {
        if (!isAdded() || getContext() == null) return;

        fetchWorkouts = new FetchWorkouts(getContext()) {
            @Override
            protected void onDataLoaded(List<Workout> data) {
                if (isAdded() && adapter != null) {
                    workouts.clear();
                    workouts.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onError(String errorMessage) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };
        fetchWorkouts.fetchWorkouts();
    }
}
