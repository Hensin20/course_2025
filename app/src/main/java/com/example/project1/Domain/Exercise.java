package com.example.project1.Domain;

public class Exercise {
    private int exerciseId;
    private int workoutId;
    private String title;
    private String videoUrl;
    private String durationSeconds;
    private String previewImageUrl;

    public Exercise(int exerciseId, int workoutId, String title, String videoUrl, String durationSeconds, String previewImageUrl) {
        this.exerciseId = exerciseId;
        this.workoutId = workoutId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.durationSeconds = durationSeconds;
        this.previewImageUrl = previewImageUrl;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(String durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }
}
