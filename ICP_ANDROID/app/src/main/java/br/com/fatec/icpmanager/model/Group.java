package br.com.fatec.icpmanager.model;
import java.io.Serializable;
import java.util.Objects;

public class Group implements Serializable {

    private String id;
    private String idTask;
    private String name;
    private boolean enable;

    public Group(){}

    public Group(String id, String idTask, String name, boolean enable) {
        this.id = id;
        this.idTask = idTask;
        this.name = name;
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTask() {
        return idTask;
    }

    public void setIdTask(String idTask) {
        this.idTask = idTask;
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