package br.com.fatec.icpmanager.fragment.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import br.com.fatec.icpmanager.adapter.ProfessorAdapter;
import br.com.fatec.icpmanager.listener.NewProjectListener;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.listener.UniversityListListener;
import br.com.fatec.icpmanager.model.Professor;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

@SuppressLint("ValidFragment")
public class ThirdPartFragment extends CustomFragment
        implements RecyclerViewClickListener, UniversityListListener {

    private List<Professor> professorList;
    private List<Upload> uploadList;

    private ProfessorAdapter filteredAdapter;
    private List<Professor> filteredProfessors;
    private List<Upload> filteredUploads;

    public ThirdPartFragment(NewProjectListener listener, Context context) {
        super(listener, context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newproject_list_selector, container, false);
        setComponents();
        return view;
    }

    private void setProfessors(List<String> universities) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("professors").exists()) {
                    professorList.clear();
                    for (DataSnapshot ds : dataSnapshot.child("professors").getChildren()) {
                        Professor professor = ds.getValue(Professor.class);
                        if (professor != null) {
                            for (String university : professor.getUniversityList())
                                if (universities.contains(university) &&
                                        !professorList.contains(professor) &&
                                        !professor.getId().equals(FirebaseHelper.getCurrentUser().getUid()))
                                    professorList.add(professor);
                        } else
                            Log.e("ERROR", "NULL PROFESSOR ON PROJECT CREATION THIRD FRAGMENT LIST");
                    }

                    for (Professor professor : professorList) {
                        Upload upload;
                        if (professor.getPicture() != null && dataSnapshot.child("profile_photo")
                                .child(professor.getId()).exists())
                            upload = dataSnapshot.child("profile_photo")
                                    .child(professor.getId()).getValue(Upload.class);
                        else
                            upload = new Upload("null");
                        uploadList.add(upload);
                    }

                    for (Professor professor : filteredProfessors)
                        if (!professorList.contains(professor))
                            filteredProfessors.remove(professor);

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
        filteredProfessors = new ArrayList<>();
        professorList = new ArrayList<>();
        uploadList = new ArrayList<>();
        filteredUploads = new ArrayList<>();

        TextView textView = view.findViewById(R.id.part_title);
        textView.setText(getString(R.string.professors));

        RecyclerView filteredRecyclerView = view.findViewById(R.id.recycler_selected_universities);
        filteredAdapter = new ProfessorAdapter(filteredProfessors, filteredUploads,
                context, this, ProfessorAdapter.REMOVE);
        filteredRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        filteredRecyclerView.setAdapter(filteredAdapter);
    }

    private void setRecycler() {
        RecyclerView universityRecyclerView = view.findViewById(R.id.recycler_universities);
        universityRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ProfessorAdapter adapter = new ProfessorAdapter(professorList, uploadList,
                context, this, ProfessorAdapter.FILTER);
        universityRecyclerView.setAdapter(adapter);

        filteredAdapter.notifyDataSetChanged();
        checkFilter();
    }

    private void checkFilter() {
        if (filteredProfessors.size() > 0)
            setFilled(true);
        else
            setFilled(false);
    }

    @Override
    public void savePart() {
        if (filteredProfessors.size() > 0) {

            bundle.clear();
            ArrayList<String> professors = new ArrayList<>();
            for (Professor professor : filteredProfessors)
                professors.add(professor.getId());

            bundle.putStringArrayList("PROFESSORS", professors);
            listener.onPartFilled(3, bundle);
        }
    }

    @Override
    public void onUniversitiesSeletected(List<String> universities) {
        setProfessors(universities);
    }

    @Override
    public void recyclerViewListClicked(View v, int position, int TAG, String TYPE) {
        if (TAG == ProfessorAdapter.FILTER) {
            if (filteredProfessors.contains(professorList.get(position))) {
                filteredProfessors.remove(professorList.get(position));
                filteredUploads.remove(uploadList.get(position));
            } else {
                filteredProfessors.add(professorList.get(position));
                filteredUploads.add(uploadList.get(position));
            }

        } else if (TAG == ProfessorAdapter.REMOVE) {
            filteredProfessors.remove(position);
            filteredUploads.remove(position);
        }

        filteredAdapter.notifyDataSetChanged();
        checkFilter();

        savePart();
    }
}