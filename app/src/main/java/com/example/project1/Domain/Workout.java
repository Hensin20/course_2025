package com.example.project1.Domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Workout {
    private String title;
    private String description;
    private String picPath;
    private String durationAll;
    private ArrayList<Lession> lessions;

    // Конструктор, гетери та сетери
    public Workout(String title, String description, String picPath, String durationAll, ArrayList<Lession> lessions) {
        this.title = title;
        this.description = description;
        this.picPath = picPath;
        this.durationAll = durationAll;
        this.lessions = lessions;
    }

    public String getTitle() { return title; }
    public String getPicPath() { return picPath; }
    public String getDurationAll() { return durationAll; }
}
