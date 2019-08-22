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
import br.com.fatec.icpmanager.adapter.UniversityAdapter;
import br.com.fatec.icpmanager.model.University;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class UniversityListActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_list);
        setComponents();
    }

    private void setComponents() {
        recyclerView = findViewById(R.id.recycler_universities_list);
        animationView = findViewById(R.id.animation_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.universities));
        getSupportActionBar().setElevation(0);

        Intent intent = getIntent();
        List<String> list = (List<String>) intent.getSerializableExtra("IDLIST");
        getUniversities(list);
    }

    private void getUniversities(List<String> list) {
        List<University> universities = new ArrayList<>();
        UniversityAdapter adapter = new UniversityAdapter(universities, this,
                null, UniversityAdapter.FILTER);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        FirebaseHelper.getUniversities(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        University university = snapshot.getValue(University.class);
                        if (university != null && university.isEnable())
                            if (list != null) {
                                if (list.contains(university.getId()))
                                    universities.add(university);
                            } else
                                universities.add(university);
                    }
                    adapter.notifyDataSetChanged();
                    setResponseLayout(universities.size());
                } else setResponseLayout(-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UNIVERSITY_LIST_ERR", databaseError.getMessage());
                Toast.makeText(UniversityListActivity.this, getString(R.string.error_get_data), Toast.LENGTH_SHORT).show();
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
