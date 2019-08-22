package br.com.fatec.icpmanager.fragment.project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.StudentAdapter;
import br.com.fatec.icpmanager.listener.NewProjectListener;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.listener.UniversityListListener;
import br.com.fatec.icpmanager.model.Student;
import br.com.fatec.icpmanager.model.Upload;

@SuppressLint("ValidFragment")
public class FifthPartFragment extends CustomFragment
        implements RecyclerViewClickListener, UniversityListListener {

    private StudentAdapter filteredAdapter;

    private List<Student> studentList;
    private List<Upload> uploadList;

    private List<Student> filteredStudents;
    private List<Upload> filteredUploads;

    public FifthPartFragment(NewProjectListener listener, Context context) {
        super(listener, context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newproject_list_selector, container, false);
        setHasOptionsMenu(true);
        setComponents();
        return view;
    }

    private void setStudents(List<String> universities) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("students").exists()) {
                    studentList.clear();
                    for (DataSnapshot ds : dataSnapshot.child("students").getChildren()) {
                        Student student = ds.getValue(Student.class);
                        if (student != null && universities.contains(student.getUniversity()) &&
                                !studentList.contains(student))
                            studentList.add(student);
                    }
                    for (Student student : studentList) {
                        Upload upload;
                        if (student.getPicture() != null && dataSnapshot.child("profile_photo")
                                .child(student.getId()).exists())
                            upload = dataSnapshot.child("profile_photo")
                                    .child(student.getId()).getValue(Upload.class);
                        else
                            upload = new Upload("null");

                        uploadList.add(upload);
                    }

                    for (Student student : filteredStudents)
                        if (!studentList.contains(student))
                            filteredStudents.remove(student);

                    setRecycler();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DATABASE_PROFESSOR_ERRO", databaseError.getMessage());
            }
        });
    }

    @Override
    public void setComponents() {
        super.setComponents();
        studentList = new ArrayList<>();
        uploadList = new ArrayList<>();
        filteredStudents = new ArrayList<>();
        filteredUploads = new ArrayList<>();

        TextView textView = view.findViewById(R.id.part_title);
        textView.setText(getString(R.string.students));
        TextView opcionalView = view.findViewById(R.id.opcional);
        opcionalView.setVisibility(View.VISIBLE);

        RecyclerView filteredRecyclerView = view.findViewById(R.id.recycler_selected_universities);
        filteredAdapter = new StudentAdapter(filteredStudents, filteredUploads,
                context, this, StudentAdapter.REMOVE);
        filteredRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        filteredRecyclerView.setAdapter(filteredAdapter);
    }

    private void setRecycler() {
        RecyclerView studentsRecyclerView = view.findViewById(R.id.recycler_universities);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        StudentAdapter adapter = new StudentAdapter(studentList, uploadList,
                context, this, StudentAdapter.FILTER);
        studentsRecyclerView.setAdapter(adapter);

        filteredAdapter.notifyDataSetChanged();
        checkFilter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.confirm_menu, menu);
        MenuItem confirm = menu.findItem(R.id.menu_confirm);
        confirm.setOnMenuItemClickListener(item -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(R.string.create_project)
                    .setMessage(getString(R.string.confirm_project_creation))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        savePart();
                        listener.onProjectInfoFilled();
                    });
            builder.create().show();
            return false;
        });
    }

    @Override
    public void savePart() {
        ArrayList<String> students = new ArrayList<>();
        if (filteredStudents.size() > 0) {
            bundle.clear();
            for (Student student : filteredStudents)
                students.add(student.getId());
        } else
            students.add(" ");

        bundle.putStringArrayList("STUDENTS", students);
        listener.onPartFilled(5, bundle);
    }


    private void checkFilter() {
        if (filteredStudents.size() > 0)
            setFilled(true);
        else
            setFilled(false);
    }

    @Override
    public void onUniversitiesSeletected(List<String> universities) {
        setStudents(universities);
    }

    @Override
    public void recyclerViewListClicked(View v, int position, int TAG, String TYPE) {
        if (TAG == StudentAdapter.FILTER) {
            if (filteredStudents.contains(studentList.get(position))) {
                filteredStudents.remove(studentList.get(position));
                filteredUploads.remove(uploadList.get(position));
            } else {
                filteredStudents.add(studentList.get(position));
                filteredUploads.add(uploadList.get(position));
            }

        } else if (TAG == StudentAdapter.REMOVE) {
            filteredStudents.remove(position);
            filteredUploads.remove(position);
        }

        filteredAdapter.notifyDataSetChanged();
        checkFilter();
        savePart();
    }
}
