package br.com.fatec.icpmanager.dao;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;


public abstract class AbstractDAO<T extends Serializable> implements GenericDAO<T>{

    protected DatabaseReference db;

    public AbstractDAO(String tableReference){
        db = FirebaseDatabase.getInstance().getReference(tableReference);
    }

    @Override
    public void save(@NonNull String id, @NonNull T entity) {
        db.child(id).setValue(entity);
    }

    @Override
    public void enable(@NonNull String id) {
        db.child(id).child("enable").setValue("true");
    }

    @Override
    public void disable(@NonNull String id) {
        db.child(id).child("enable").setValue("false");
    }

    @Override
    public void delete(@NonNull String id) {
        db.child(id).removeValue();
    }

    @Override
    public String newKey() {
        return db.push().getKey();
    }

}