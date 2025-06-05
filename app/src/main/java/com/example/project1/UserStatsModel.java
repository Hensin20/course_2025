package com.example.project1;

public class UserStatsModel {
    private int userId;
    private int steps;
    private float calories;
    private float distanceKm;
    private String date; // ✅ Використовуємо String для дати

    public int getUserId() { return userId; }
    public int getSteps() { return steps; }
    public float getCalories() { return calories; }
    public float getDistanceKm() { return distanceKm; }
    public String getDate() { return date; }
}

