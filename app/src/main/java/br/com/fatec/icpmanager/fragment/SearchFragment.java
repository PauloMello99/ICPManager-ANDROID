package br.com.fatec.icpmanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.activity.ProfessorActivity;
import br.com.fatec.icpmanager.activity.StudentActivity;
import br.com.fatec.icpmanager.activity.UniversityActivity;
import br.com.fatec.icpmanager.adapter.ProfessorAdapter;
import br.com.fatec.icpmanager.adapter.StudentAdapter;
import br.com.fatec.icpmanager.adapter.UniversityAdapter;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.model.Professor;
import br.com.fatec.icpmanager.model.Student;
import br.com.fatec.icpmanager.model.University;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class SearchFragment extends Fragment implements PopupMenu.OnMenuItemClickListener,
        RecyclerViewClickListener {

    private View view;
    private Context context;
    private LottieAnimationView emptyListView;
    private RecyclerView recyclerView;
    private EditText searchEditText;

    private int SELECTED;
    private List<Upload> uploads;
    private List<University> universities;
    private List<Student> students;
    private List<Professor> professors;

    public SearchFragment() {}

    public SearchFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        setComponents();
        setResponseLayout(0);
        return view;
    }

    private void setComponents() {
        SELECTED = -1;
        uploads = new ArrayList<>();
        universities = new ArrayList<>();
        students = new ArrayList<>();
        professors = new ArrayList<>();

        searchEditText = view.findViewById(R.id.searchView);
        emptyListView = view.findViewById(R.id.animation_empty_list);
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        ImageView filterView = view.findViewById(R.id.filter_button);
        filterView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.setOnMenuItemClickListener(SearchFragment.this);
            popupMenu.inflate(R.menu.filter_menu);
            popupMenu.show();
        });

        searchEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence text, int start, int before, int count) {
                        query(text);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

        setRecycler();
    }

    private void query(CharSequence text) {
        switch (SELECTED) {
            case 1:
                // Universidades
                UniversityAdapter universityAdapter;
                if (text.toString().length() == 0)
                    universityAdapter = new UniversityAdapter(universities, context,
                            null, UniversityAdapter.FILTER);
                else {
                    List<University> filteredUniversities = new ArrayList<>();
                    for (University university : universities)
                        if (university.getName().toLowerCase().contains(text.toString().toLowerCase()) ||
                                university.getInitials().toLowerCase().contains(text.toString().toLowerCase()))
                            filteredUniversities.add(university);
                    universityAdapter = new UniversityAdapter(filteredUniversities,
                            context, null, UniversityAdapter.FILTER);
                }
                recyclerView.setAdapter(universityAdapter);
                universityAdapter.notifyDataSetChanged();
                break;
            case 2:
                ProfessorAdapter professorAdapter;
                if (text.toString().length() == 0)
                    professorAdapter = new ProfessorAdapter(professors, uploads,
                            context, null, ProfessorAdapter.FILTER);
                else {
                    List<Professor> filteredProfessors = new ArrayList<>();
                    List<Upload> filteredUploads = new ArrayList<>();
                    for (Professor professor : professors) {
                        if (professor.getName().toLowerCase().contains(text.toString().toLowerCase()) ||
                                professor.getEmail().toLowerCase().contains(text.toString().toLowerCase())) {
                            filteredProfessors.add(professor);
                            Upload upload = new Upload(professor.getId());
                            if (uploads.contains(upload)) filteredUploads.add(upload);
                            else filteredUploads.add(upload);
                            filteredUploads.add(new Upload("null"));
                        }
                    }
                    professorAdapter = new ProfessorAdapter(filteredProfessors,
                            filteredUploads, context, null, ProfessorAdapter.FILTER);
                }
                recyclerView.setAdapter(professorAdapter);
                professorAdapter.notifyDataSetChanged();
                break;
            case 3:
                StudentAdapter studentAdapter;
                if (text.toString().length() == 0)
                        studentAdapter = new StudentAdapter(students, uploads, context, null, StudentAdapter.FILTER);
                else {
                    List<Student> filteredStudents = new ArrayList<>();
                    List<Upload> filteredUploads = new ArrayList<>();
                    for (Student student : students) {
                        if (student.getName().toLowerCase().contains(text.toString().toLowerCase()) ||
                                student.getEmail().toLowerCase().contains(text.toString().toLowerCase())) {
                            filteredStudents.add(student);
                            Upload upload = new Upload(student.getId());
                            if (uploads.contains(upload)) filteredUploads.add(upload);
                            else filteredUploads.add(upload);
                            filteredUploads.add(new Upload("null"));
                        }
                    }
                    studentAdapter = new StudentAdapter(filteredStudents, filteredUploads, context,
                            null, StudentAdapter.FILTER);
                }
                recyclerView.setAdapter(studentAdapter);
                studentAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        searchEditText.setText("");

        switch (item.getItemId()) {
            case R.id.menu_universities:
                getUniversities();
                searchEditText.setHint(R.string.universities);
                break;
            case R.id.menu_professors:
                getProfessors();
                searchEditText.setHint(R.string.professors);
                break;
            case R.id.menu_students:
                getStudents();
                searchEditText.setHint(R.string.students);
                break;
        }
        return false;
    }

    private void getStudents() {
        FirebaseHelper.getRoot(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    students.clear();
                    uploads.clear();
                    for (DataSnapshot child : dataSnapshot.child("students").getChildren()) {
                        Student student = child.getValue(Student.class);
                        if (student != null && student.isEnable()) {
                            students.add(student);
                            Upload upload;
                            if (dataSnapshot.child("profile_photo").child(student.getId()).exists())
                                upload = dataSnapshot.child("profile_photo").child(student.getId()).getValue(Upload.class);
                            else upload = new Upload("null");
                            uploads.add(upload);
                        }
                    }
                    StudentAdapter adapter = new StudentAdapter(students, uploads, context,
                            SearchFragment.this, StudentAdapter.FILTER);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    setResponseLayout(students.size());
                } else setResponseLayout(-1);
                SELECTED = 3;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("STUDENT_FILTER_ERR", databaseError.getMessage());
                setResponseLayout(-1);
            }
        });
    }

    private void getProfessors() {
        FirebaseHelper.getRoot(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    professors.clear();
                    uploads.clear();
                    for (DataSnapshot child : dataSnapshot.child("professors").getChildren()) {
                        Professor professor = child.getValue(Professor.class);
                        if (professor != null && professor.isEnable()) {
                            professors.add(professor);
                            Upload upload;
                            if (dataSnapshot.child("profile_photo").child(professor.getId()).exists())
                                upload = dataSnapshot.child("profile_photo").child(professor.getId()).getValue(Upload.class);
                            else upload = new Upload("null");
                            uploads.add(upload);
                        }
                    }
                    ProfessorAdapter adapter = new ProfessorAdapter(professors, uploads, context,
                            SearchFragment.this, ProfessorAdapter.FILTER);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    setResponseLayout(professors.size());
                } else setResponseLayout(-1);
                SELECTED = 2;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROFESSORS_FILTER_ERR", databaseError.getMessage());
                setResponseLayout(-1);
            }
        });
    }

    private void getUniversities() {
        FirebaseHelper.getUniversities(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    universities.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        University university = child.getValue(University.class);
                        if (university != null && university.isEnable())
                            universities.add(university);
                    }
                    UniversityAdapter adapter = new UniversityAdapter(universities, context,
                            SearchFragment.this, UniversityAdapter.FILTER);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    setResponseLayout(universities.size());
                } else setResponseLayout(-1);
                SELECTED = 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UNIVERSITIES_FILTER_ERR", databaseError.getMessage());
                setResponseLayout(-1);
            }
        });
    }

    private void setResponseLayout(int size) {
        if (size > 0) {
            emptyListView.pauseAnimation();
            emptyListView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else if (size == 0) {
            emptyListView.setAnimation("no_items_search.json");
            emptyListView.playAnimation();
            recyclerView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setAnimation("error.json");
            emptyListView.playAnimation();
            recyclerView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }
    }

    private void setRecycler() {
        RecyclerView.LayoutManager layout = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
    }

    @Override
    public void recyclerViewListClicked(View v, int position, int TAG, String TYPE) {
        Intent intent = null;
        switch (TYPE) {
            case "PROFESSOR":
                intent = new Intent(context, ProfessorActivity.class);
                intent.putExtra("ID",professors.get(position).getId());
                break;
            case "STUDENT":
                intent = new Intent(context, StudentActivity.class);
                intent.putExtra("ID",students.get(position).getId());
                break;
            case "UNIVERSITY":
                intent = new Intent(context, UniversityActivity.class);
                intent.putExtra("ID",universities.get(position).getId());
                break;
        }
        if (intent != null) startActivity(intent);
    }
}
