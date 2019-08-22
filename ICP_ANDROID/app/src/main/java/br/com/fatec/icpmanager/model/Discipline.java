package br.com.fatec.icpmanager.model;
import java.io.Serializable;
import java.util.Objects;

public class Discipline implements Serializable {

    private String id;
    private String name;
    private String description;
    private boolean enable;

    public  Discipline(){}

    public Discipline(String id, String name, String description, boolean enable) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
