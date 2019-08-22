package br.com.fatec.icpmanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.fatec.icpmanager.fragment.ProjectsFragment;

public class FirebaseHelper {

    public static int NONE = -1;
    public static int STUDENT = 1;
    public static int PROFESSOR = 2;
    public static int COORDINATOR = 3;

    private static DatabaseReference dbReference;
    private static StorageReference stgReference;

    private static String COORDINATOR_PATH = "coordinators";
    private static String PHASE_PATH = "phases";
    private static String PROFESSOR_PATH = "professors";
    private static String STUDENT_PATH = "students";
    private static String PHOTO_PATH = "profile_photo";
    private static String PROJECT_PATH = "projects";
    private static String UNIVERSITY_PATH = "universities";
    private static String USER_PATH = "users";

    public static boolean isInternetConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info!=null)return info.isConnected();
        else return false;
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    public static DatabaseReference getRoot(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference();
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getCoordinator(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(COORDINATOR_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getCoordinatorRealTime(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(COORDINATOR_PATH);
        dbReference.child(id).addValueEventListener(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getCoordinators(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(COORDINATOR_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getPhase(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PHASE_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getPhases(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PHASE_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getProfessor(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PROFESSOR_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getProfessorRealTime(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PROFESSOR_PATH);
        dbReference.child(id).addValueEventListener(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getProfessors(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PROFESSOR_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getPhoto(String id) {
        dbReference = FirebaseDatabase.getInstance().getReference(PHOTO_PATH);
        return dbReference.child(id);
    }

    public static DatabaseReference getPhoto(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PHOTO_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getPhotoRealTime(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PHOTO_PATH);
        dbReference.child(id).addValueEventListener(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getPhotos(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PHOTO_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getProject(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PROJECT_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getProjects(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PROJECT_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getProjectsRealTime(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(PROJECT_PATH);
        dbReference.addValueEventListener(listener);
        return dbReference;
    }

    public static DatabaseReference getStudent(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(STUDENT_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getStudentRealTime(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(STUDENT_PATH);
        dbReference.child(id).addValueEventListener(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getStudents(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(STUDENT_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getUniversity(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(UNIVERSITY_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getUniversityRealTime(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(UNIVERSITY_PATH);
        dbReference.child(id).addValueEventListener(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getUniversities(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(UNIVERSITY_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static DatabaseReference getUniversitiesRealTime(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(UNIVERSITY_PATH);
        dbReference.addValueEventListener(listener);
        return dbReference;
    }

    public static DatabaseReference getUser(String id, ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(USER_PATH);
        dbReference.child(id).addListenerForSingleValueEvent(listener);
        return dbReference.child(id);
    }

    public static DatabaseReference getUsers(ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(USER_PATH);
        dbReference.addListenerForSingleValueEvent(listener);
        return dbReference;
    }

    public static UploadTask setPhotoStorage(String id, String photoExtension, Uri file) {
        stgReference = FirebaseStorage.getInstance().getReference(PHOTO_PATH);
        return stgReference.child(id + photoExtension).putFile(file);
    }

    public static StorageReference getPhotoStorage(String idNextension){
        stgReference = FirebaseStorage.getInstance().getReference(PHOTO_PATH);
        return stgReference.child(idNextension);
    }

    public static DatabaseReference getNotification(String userId,String notificationId,ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(USER_PATH);
        dbReference.child(userId).child(notificationId).addValueEventListener(listener);
        return dbReference.child(userId).child(notificationId);
    }

    public static DatabaseReference getNotifications(String userId,ValueEventListener listener) {
        dbReference = FirebaseDatabase.getInstance().getReference(USER_PATH);
        dbReference.child(userId).child("notifications").addValueEventListener(listener);
        return dbReference;
    }

}