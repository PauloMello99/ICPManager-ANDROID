package br.com.fatec.icpmanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.ProjectAdapter;
import br.com.fatec.icpmanager.listener.DataLoadListener;
import br.com.fatec.icpmanager.model.Project;

public class ProjectListFragment extends Fragment implements DataLoadListener<Project> {

    private Context context;
    private View view;

    private ProjectAdapter adapter;
    private List<Project> projects;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;

    public ProjectListFragment() {}

    public ProjectListFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_project_list, container, false);
        setComponents();
        return view;
    }

    private void setComponents() {
        projects = new ArrayList<>();
        animationView = view.findViewById(R.id.animation_list);
        recyclerView = view.findViewById(R.id.recycler_project);
        setRecycler();
    }

    private void setRecycler() {
        RecyclerView.LayoutManager layout = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
    }

    DataLoadListener<Project> getDataLoadListener() {
        return this;
    }

    @Override
    public void onDataLoaded(List<Project> list) {
        projects = list;
        adapter = new ProjectAdapter(projects, context);
        setRecycler();
        setResponseLayout(projects.size());
    }

    @Override
    public void onDataCancelled(String error) {
        Log.e("PROJECT_LIST_ERR", error);
        setResponseLayout(-1);
    }

    private void setResponseLayout(int size) {
        if (size > 0) {
            animationView.pauseAnimation();
            animationView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else if (size == 0) {
            animationView.setAnimation("no_items_search.json");
            animationView.playAnimation();
            recyclerView.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
        } else {
            animationView.setAnimation("error.json");
            animationView.playAnimation();
            recyclerView.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
        }
    }
}