package br.com.fatec.icpmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.fragment.UniversitySelectorFragment;
import br.com.fatec.icpmanager.listener.DataLoadListener;
import br.com.fatec.icpmanager.model.University;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class UniversitySelectorActivity extends AppCompatActivity implements DataLoadListener<University> {

    private int type;
    private List<String> universitiesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_selector);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.edit));
        getSupportActionBar().setElevation(0);

        universitiesId = new ArrayList<>();

        Intent intent = getIntent();
        type = intent.getIntExtra("USERTYPE", -1);
        int option = intent.getIntExtra("OPTION", UniversitySelectorFragment.MANY);
        List<String> selectedUniversities = intent.getStringArrayListExtra("UNIVERSITIES");

        UniversitySelectorFragment fragment = new UniversitySelectorFragment(this,
                UniversitySelectorActivity.this, option,selectedUniversities);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_university_select, fragment)
                .commit();
    }

    @Override
    public void onDataLoaded(List<University> list) {
        universitiesId.clear();
        for (University university : list) universitiesId.add(university.getId());
    }

    @Override
    public void onDataCancelled(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        if (item.getItemId() == R.id.menu_confirm) {
            Intent returnIntent = new Intent();
            if(universitiesId.size()>0){
                if(type == FirebaseHelper.STUDENT)
                    returnIntent.putExtra("UNIVERSITY",universitiesId.get(0));
                else if (type == FirebaseHelper.PROFESSOR || type == FirebaseHelper.COORDINATOR)
                    returnIntent.putExtra("UNIVERSITY", (Serializable) universitiesId);
                setResult(Activity.RESULT_OK,returnIntent);
            }else setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        return true;
    }
}