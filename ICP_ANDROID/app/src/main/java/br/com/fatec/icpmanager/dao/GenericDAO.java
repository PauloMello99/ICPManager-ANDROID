package br.com.fatec.icpmanager.dao;

import androidx.annotation.NonNull;

import java.io.Serializable;

public interface GenericDAO<T extends Serializable> {

    void save(@NonNull String id, @NonNull T entity);

    void enable(@NonNull String id);

    void disable(@NonNull String id);

    void delete(@NonNull String id);

    String newKey();

}