package br.com.fatec.icpmanager.model;

import java.io.Serializable;
import java.util.Objects;

public class University implements Serializable {

    private String id;
    private String name;
    private String initials;
    private String country;
    private String state;
    private String department;
    private String picture;
    private boolean enable;

    public University(){}

    public University(String id, String name, String initials, String country, String state, String department, String picture, boolean enable) {
        this.id = id;
        this.name = name;
        this.initials = initials;
        this.country = country;
        this.state = state;
        this.department = department;
        this.picture = picture;
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "University{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", initials='" + initials + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", department='" + department + '\'' +
                ", picture='" + picture + '\'' +
                ", enable=" + enable +
                '}';
    }
}