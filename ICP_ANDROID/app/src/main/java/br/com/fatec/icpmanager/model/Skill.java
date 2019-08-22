package br.com.fatec.icpmanager.model;
import java.io.Serializable;
import java.util.Objects;

public class Skill implements Serializable {

    private String id;
    private String name;
    private boolean enable;

    public Skill(){}

    public Skill(String id, String name, boolean enable) {
        this.id = id;
        this.name = name;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}