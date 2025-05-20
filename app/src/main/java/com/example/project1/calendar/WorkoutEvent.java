package com.example.project1.calendar;

public class WorkoutEvent {
    private String date;
    private String title;
    private String time;
    public WorkoutEvent(String date, String title, String time) {
        this.date = date;
        this.title = title;
        this.time = time;
    }
    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

}
