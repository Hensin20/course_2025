package com.example.project1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_training, container, false);

        // Ініціалізація RecyclerView
        recyclerView = view.findViewById(R.id.list_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter(adapter);

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

        FetchWorkouts fetchWorkouts = new FetchWorkouts(getContext(), recyclerView) {

            protected void onDataLoaded(List<Workout> workouts) {
                Log.e("loadWorkouts", "Дані завантажені: " + workouts.size() + " елементів");

                Fragment_training.this.workouts.clear();
                Fragment_training.this.workouts.addAll(workouts);

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    Log.e("loadWorkouts", "Адаптер оновлено!");
                } else {
                    Log.e("loadWorkouts", "⚠️ Адаптер == null!");
                }
            }

            protected void onError(String errorMessage) {
                Log.e("loadWorkouts", "Помилка завантаження: " + errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

        Log.e("loadWorkouts", "Виклик fetchWorkouts.fetchWorkouts()");
        fetchWorkouts.fetchWorkouts();
    }
}