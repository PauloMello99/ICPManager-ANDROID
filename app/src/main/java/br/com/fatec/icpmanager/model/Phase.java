package br.com.fatec.icpmanager.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Phase implements Serializable {

    private String id;
    private String title;
    private String description;
    private String endDate;
    private ArrayList<String> tasks;
    private boolean enable;

    public Phase() { }

    public Phase(String title, String description, String endDate) {
        this.id = " ";
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.tasks = new ArrayList<>();
        this.tasks.add(" ");
        this.enable = true;
    }

    public Phase(String id, String title, String description, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.tasks = new ArrayList<>();
        this.tasks.add(" ");
        this.enable = true;
    }

    public Phase(String id, String title, String description, String endDate, ArrayList<String> tasks, boolean enable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
        this.tasks = tasks;
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}