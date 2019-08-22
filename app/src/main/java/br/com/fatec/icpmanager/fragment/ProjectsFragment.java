package br.com.fatec.icpmanager.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.activity.CreateProjectActivity;
import br.com.fatec.icpmanager.adapter.ViewPagerAdapter;
import br.com.fatec.icpmanager.listener.DataLoadListener;
import br.com.fatec.icpmanager.model.Project;
import br.com.fatec.icpmanager.utils.CustomViewPager;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class ProjectsFragment extends Fragment implements ValueEventListener {

    private View view;
    private Context context;
    private List<DataLoadListener<Project>> listeners;
    private List<List<Project>> lists;
    private boolean showFab;

    public ProjectsFragment() {
    }

    public ProjectsFragment(Context context, int type) {
        this.context = context;
        // Se o tipo de usuário é válido e não é estudante
        showFab = type == FirebaseHelper.PROFESSOR || type == FirebaseHelper.COORDINATOR;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_projects, container, false);
        setComponents();
        return view;
    }

    @SuppressLint("RestrictedApi")
    private void setComponents() {
        CustomViewPager viewPager = view.findViewById(R.id.view_pager_projects);
        TabLayout tabLayout = view.findViewById(R.id.tab_projects);
        FloatingActionButton fabAddProject = view.findViewById(R.id.fab_add_project);

        if (showFab) fabAddProject.setVisibility(View.VISIBLE);
        fabAddProject.setOnClickListener(v -> startActivity(new Intent(context, CreateProjectActivity.class)));

        viewPager.setPagingEnabled(false);
        listeners = new ArrayList<>();
        lists = new ArrayList<>();
        // Lista de Projeto para cada filtro {Em Andamento;Finalizado}
        lists.add(new ArrayList<>());
        lists.add(new ArrayList<>());

        ProjectListFragment progress = new ProjectListFragment(context);
        ProjectListFragment finished = new ProjectListFragment(context);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getFragmentManager());
        pagerAdapter.addFragment(progress, getString(R.string.in_progress));
        pagerAdapter.addFragment(finished, getString(R.string.finished));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        listeners.add(progress.getDataLoadListener());
        listeners.add(finished.getDataLoadListener());

        FirebaseHelper.getProjectsRealTime(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            for (List<Project> list : lists) list.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Project project = snapshot.getValue(Project.class);
                if (project != null && project.isEnable())
                    if (!project.isFinished())
                        lists.get(0).add(project);
                    else
                        lists.get(1).add(project);
            }
            int i = 0;
            for (DataLoadListener<Project> listener : listeners) {
                listener.onDataLoaded(lists.get(i));
                i++;
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        for (DataLoadListener<Project> listener : listeners)
            listener.onDataCancelled(databaseError.getMessage());
    }
}