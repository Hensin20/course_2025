package com.example.project1.trenning;

import java.util.ArrayList;

public class Workout {
    private String workoutId;
    private String title;
    private String description;
    private String exercise;
    private String picPath;
    private String durationAll;
    private ArrayList<Exercise> lessions;

    // Конструктор, гетери та сетери
    public Workout(String workoutId, String title, String description, String exercise, String picPath, String durationAll, ArrayList<Exercise> lessions) {
        this.title = title;
        this.description = description;
        this.exercise = exercise;
        this.picPath = picPath;
        this.durationAll = durationAll;
        this.lessions = lessions;
    }

    public String getWorkoutId() {return workoutId;}

    public String getTitle() { return title; }
    public String getDescription() {return description;}
    public String getPicPath() { return picPath; }
    public String getExercise() {return exercise;}
    public String getDurationAll() { return durationAll; }
}
