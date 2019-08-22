package br.com.fatec.icpmanager.dao;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.model.Project;

public class ProjectDAO extends AbstractDAO<Project> {

    public ProjectDAO() {
        super("projects");
    }

    public void addUser(String projectId, String id, int userType) {
        List<String> idList = new ArrayList<>();
        if (userType == 1) {
            db.child(projectId).child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String student = snapshot.getValue(String.class);
                            if (student != null && !student.trim().isEmpty() && !student.equals(id))
                                idList.add(student);
                        }
                        idList.add(id);
                        db.child(projectId).child("students").setValue(idList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERROR_ADD_PROJECT_USER", databaseError.getMessage());
                }
            });
        } else if (userType == 2) {
            db.child(projectId).child("professors").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String professor = snapshot.getValue(String.class);
                            if (professor != null && !professor.trim().isEmpty() && !professor.equals(id))
                                idList.add(professor);
                        }
                        idList.add(id);
                        db.child(projectId).child("professors").setValue(idList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERROR_ADD_PROJECT_USER", databaseError.getMessage());
                }
            });
        }
    }

    public void enableProject(String projectId){
        db.child(projectId).child("enable").setValue(true);
    }
}