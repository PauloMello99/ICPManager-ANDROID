package br.com.fatec.icpmanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {

    private String id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private List<String> universities;
    private List<String> students;
    private List<String> professors;
    private List<Phase> phases;
    private boolean finished;
    private boolean enable;

    public Project() { }

    public Project(String id, String title, String description, String startDate, String endDate,
                   List<String> universities, List<String> students, List<String> professors,
                   List<Phase> phases, boolean finished, boolean enable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.universities = universities;
        this.students = students;
        this.professors = professors;
        this.phases = phases;
        this.finished = finished;
        this.enable = enable;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases(List<Phase> phases) {
        this.phases = phases;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getUniversities() {
        return universities;
    }

    public void setUniversities(List<String> universities) {
        this.universities = universities;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public List<String> getProfessors() {
        return professors;
    }

    public void setProfessors(List<String> professors) {
        this.professors = professors;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", finished=" + finished +
                ", enable=" + enable +
                '}';
    }
}