package br.com.fatec.icpmanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.UniversityAdapter;
import br.com.fatec.icpmanager.fragment.project.CustomFragment;
import br.com.fatec.icpmanager.listener.DataLoadListener;
import br.com.fatec.icpmanager.listener.NewProjectListener;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.model.University;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class UniversitySelectorFragment extends CustomFragment
        implements RecyclerViewClickListener {

    public static int ONE_ONLY = 0;
    public static int MANY = 1;
    private int option = 1;

    private UniversityAdapter filteredAdapter;
    private DataLoadListener<University> loadListener;

    private List<University> universities;
    private List<University> filteredUniversities;
    private UniversityAdapter adapter;
    private List<String> selectedUniversities;

    private LinearLayout layoutTitle;

    public UniversitySelectorFragment(NewProjectListener projectListener, Context context, int selectedOption) {
        super(projectListener,context);
        if (selectedOption == 1 || selectedOption == 0) option = selectedOption;
    }

    public UniversitySelectorFragment(DataLoadListener<University> loadListener, Context context, int selectedOption, List<String> universitiesId) {
        super(null, context);
        this.loadListener = loadListener;
        if (selectedOption == 1 || selectedOption == 0)
            option = selectedOption;
        selectedUniversities = universitiesId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_university_selector, container, false);
        setComponents();
        return view;
    }

    private void setUniversities() {
        FirebaseHelper.getUniversities(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                universities.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    University university = ds.getValue(University.class);
                    if (university != null && university.isEnable()) {
                        universities.add(university);
                        if (selectedUniversities != null && selectedUniversities.contains(university.getId()))
                            filteredUniversities.add(university);
                    }
                }
                adapter.notifyDataSetChanged();
                filteredAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SELECT_UNIVERSITY_ERR", databaseError.getMessage());
                if (loadListener != null)
                    loadListener.onDataCancelled(databaseError.getMessage());
            }
        });
    }

    @Override
    public void setComponents() {
        super.setComponents();
        RecyclerView universityRecyclerView = view.findViewById(R.id.recycler_universities);
        RecyclerView filteredRecyclerView = view.findViewById(R.id.recycler_selected_universities);
        layoutTitle = view.findViewById(R.id.layout_title);

        universities = new ArrayList<>();
        filteredUniversities = new ArrayList<>();

        universityRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        filteredRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));

        adapter = new UniversityAdapter(universities, context,
                this, UniversityAdapter.FILTER);
        filteredAdapter = new UniversityAdapter(filteredUniversities, context,
                this, UniversityAdapter.REMOVE);

        universityRecyclerView.setAdapter(adapter);
        filteredRecyclerView.setAdapter(filteredAdapter);
        setUniversities();
    }

    @Override
    public void savePart() {
        bundle.clear();
        ArrayList<String> universities = new ArrayList<>();

        if (filteredUniversities.size() > 0)
            for (University university : filteredUniversities)
                universities.add(university.getId());
        else
            universities.add("");

        bundle.putStringArrayList("UNIVERSITIES",universities);
        listener.onPartFilled(2,bundle);
    }

    @Override
    public void recyclerViewListClicked(View v, int position, int TAG, String TYPE) {
        if (TAG == UniversityAdapter.FILTER) {
            if (filteredUniversities.contains(universities.get(position)))
                filteredUniversities.remove(universities.get(position));
            else if (option == MANY)
                filteredUniversities.add(universities.get(position));
            else {
                filteredUniversities.clear();
                filteredUniversities.add(universities.get(position));
            }
        } else if (TAG == UniversityAdapter.REMOVE)
            filteredUniversities.remove(position);

        filteredAdapter.notifyDataSetChanged();

        if (loadListener != null) loadListener.onDataLoaded(filteredUniversities);

        if(listener!=null) {
            if(filteredUniversities.size() > 0)
                setFilled(true);
            else
                setFilled(false);
            savePart();
        }
    }

    public void setTitleVisibility(boolean showTitle){
        if(layoutTitle!=null){
            if(showTitle)
                layoutTitle.setVisibility(View.VISIBLE);
            else
                layoutTitle.setVisibility(View.GONE);
        }
    }
}