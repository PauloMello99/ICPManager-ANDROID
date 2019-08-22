package br.com.fatec.icpmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.ProjectAdapter;
import br.com.fatec.icpmanager.model.Project;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class ProjectListActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        setComponents();

        Intent intent = getIntent();
        int userType = intent.getIntExtra("USERTYPE", FirebaseHelper.NONE);
        String id = intent.getStringExtra("USERID");

        if (userType != FirebaseHelper.NONE && id != null) getProjects(userType, id);
        else {
            Toast.makeText(this, getString(R.string.error_get_data), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setComponents() {
        recyclerView = findViewById(R.id.recycler_projects_user);
        animationView = findViewById(R.id.animation_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(getString(R.string.projects));
    }

    private void getProjects(int userType, String id) {
        List<Project> projectList = new ArrayList<>();
        ProjectAdapter adapter = new ProjectAdapter(projectList, this);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        FirebaseHelper.getProjects(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    projectList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Project project = snapshot.getValue(Project.class);
                        if (project != null && project.isEnable())
                            switch (userType) {
                                case 1:
                                    if (project.getStudents().contains(id))
                                        projectList.add(project);
                                    break;
                                case 2:
                                    if (project.getProfessors().contains(id))
                                        projectList.add(project);
                                    break;
                                case 3:
                                    if (project.getProfessors().contains(id))
                                        projectList.add(project);
                                    break;
                                default:
                                    projectList.add(project);
                                    break;
                            }
                    }
                    adapter.notifyDataSetChanged();
                    setResponseLayout(projectList.size());
                } else setResponseLayout(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROJECT_LIST_AC_ERR", databaseError.getMessage());
                Toast.makeText(ProjectListActivity.this,
                        getString(R.string.error_get_data), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return true;
    }
}