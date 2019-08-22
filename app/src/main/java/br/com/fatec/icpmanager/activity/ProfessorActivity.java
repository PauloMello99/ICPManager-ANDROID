package br.com.fatec.icpmanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.model.Professor;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.FirebaseHelper;
import br.com.fatec.icpmanager.utils.Helper;

public class ProfessorActivity extends AppCompatActivity {

    private ImageView photoView;
    private TextView nameTextView;
    private TextView genderTextView;
    private TextView birthDateView;
    private TextView emailTextView;
    private TextView degreeTextView;
    private TextView bioTextView;
    private ImageView facebookView;
    private ImageView skypeView;

    private String id;
    private boolean isCurrentUser;
    private Professor professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        setComponents();
    }

    private void setComponents() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));
        getSupportActionBar().setElevation(0);

        photoView = findViewById(R.id.image_view_photo);
        nameTextView = findViewById(R.id.text_name);
        genderTextView = findViewById(R.id.text_gender_professor);
        birthDateView = findViewById(R.id.text_birth_date);
        emailTextView = findViewById(R.id.text_email);
        facebookView = findViewById(R.id.facebook_view);
        skypeView = findViewById(R.id.skype_view);
        degreeTextView = findViewById(R.id.text_degree_professor);
        bioTextView = findViewById(R.id.text_bio_professor);
        ImageView projectsView = findViewById(R.id.image_view_projects);
        ImageView universitiesView = findViewById(R.id.image_view_universities);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        isCurrentUser = intent.getBooleanExtra("ISUSER", false);
        fetchProfessor();

        projectsView.setOnClickListener(v -> {
            Intent intentProjects = new Intent(ProfessorActivity.this, ProjectListActivity.class);
            intentProjects.putExtra("USERID",id);
            intentProjects.putExtra("USERTYPE", FirebaseHelper.PROFESSOR);
            startActivity(intentProjects);
        });

        universitiesView.setOnClickListener(v ->{
            Intent intentUniversities = new Intent(ProfessorActivity.this, UniversityListActivity.class);
            intentUniversities.putExtra("IDLIST", (Serializable) professor.getUniversityList());
            startActivity(intentUniversities);
        });
    }

    private void fetchProfessor() {
        //Pega foto
        FirebaseHelper.getPhoto(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    if (upload != null && !upload.getPhoto().equals("null"))
                        Picasso.get()
                                .load(upload.getPhoto())
                                .error(R.drawable.error_icon_32)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(photoView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROFILE_PROFESSOR_ERR", "IMAGE LOAD ERR--> " + databaseError.getMessage());
            }
        });

        //Pega perfil do usuário
        FirebaseHelper.getProfessorRealTime(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    professor = dataSnapshot.getValue(Professor.class);
                    if (professor != null) {
                        nameTextView.setText(professor.getName());
                        genderTextView.setText(professor.getGender());
                        birthDateView.setText(professor.getBirthDate());
                        emailTextView.setText(professor.getEmail());
                        degreeTextView.setText(professor.getDegree());
                        bioTextView.setText(professor.getBio());

                        if (!professor.getFacebookUrl().isEmpty()){
                            facebookView.setBackground(getResources().getDrawable(R.drawable.facebook_logo_blue));
                            facebookView.setOnClickListener(v ->
                                    Helper.openFacebookProfile(professor.getFacebookUrl(),ProfessorActivity.this));
                        }
                        else
                            facebookView.setBackground(getResources().getDrawable(R.drawable.facebook_logo_grey));

                        if (!professor.getSkypeUrl().isEmpty()){
                            skypeView.setBackground(getResources().getDrawable(R.drawable.skype_logo_blue));
                            skypeView.setOnClickListener(v ->
                                    Helper.openSkypeProfile(professor.getSkypeUrl(),ProfessorActivity.this));
                        }
                        else
                            skypeView.setBackground(getResources().getDrawable(R.drawable.skype_logo_grey));
                    }
                } else {
                    Log.e("PROFILE_STUDENT_ERR", "DATA SNAPSHOT DOES NOT EXIST");
                    Toast.makeText(ProfessorActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROFILE_PROFESSOR_ERR", "DATA SNAPSHOT DOES NOT EXIST");
                Toast.makeText(ProfessorActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        menu.getItem(0).setVisible(isCurrentUser);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            Intent intent = new Intent(ProfessorActivity.this, ProfessorEditActivity.class);
            intent.putExtra("PROFESSOR",professor);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
