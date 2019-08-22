package br.com.fatec.icpmanager.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Notification implements Serializable {

    private String id;
    private String projectId;
    private String to;
    private String title;
    private String body;
    private String flag;
    private boolean read;

    public Notification() {}

    public Notification(String projectId, String to, String title, String body, String flag, boolean read) {
        this.projectId = projectId;
        this.to = to;
        this.title = title;
        this.body = body;
        this.read = read;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notification{" +
                "projectId='" + projectId + '\'' +
                ", to='" + to + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", read=" + read +
                '}';
    }
}
