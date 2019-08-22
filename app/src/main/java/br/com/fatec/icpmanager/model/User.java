package br.com.fatec.icpmanager.model;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

    private String id;
    private String type;
    private String mToken;
    private HashMap<String, Notification> notifications;

    public User() {
        notifications = new HashMap<>();
    }

    public User(String uid, String type, String mToken) {
        this.id = uid;
        this.type = type;
        this.mToken = mToken;
        notifications = new HashMap<>();
    }

    public User(String id, String type, String mToken, HashMap<String, Notification> notifications) {
        this.id = id;
        this.type = type;
        this.mToken = mToken;
        this.notifications = notifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        this.id = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(HashMap<String, Notification> notifications) {
        this.notifications = notifications;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", mToken='" + mToken + '\'' +
                ", notifications=" + notifications +
                '}';
    }
}
