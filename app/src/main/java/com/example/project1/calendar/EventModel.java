package com.example.project1.calendar;
import java.io.Serializable;

public class EventModel implements Serializable {
    private int id;
    private String title;
    private String eventDate;
    private String eventTime;

    public EventModel(int id, String title, String eventDate, String eventTime) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
