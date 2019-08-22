package br.com.fatec.icpmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.model.Student;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.FirebaseHelper;
import br.com.fatec.icpmanager.utils.Helper;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentActivity extends AppCompatActivity {

    private ImageView photoView;
    private TextView nameTextView;
    private TextView universityView;
    private TextView genderTextView;
    private TextView birthDateView;
    private TextView emailTextView;
    private ImageView facebookView;
    private ImageView skypeView;

    private String id;
    private boolean isCurrentUser;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        setComponents();
    }

    private void setComponents() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.profile));
        getSupportActionBar().setElevation(0);

        photoView = findViewById(R.id.image_view_photo);
        universityView = findViewById(R.id.text_university);
        ImageView projectsView = findViewById(R.id.image_view_projects);
        nameTextView = findViewById(R.id.text_name);
        genderTextView = findViewById(R.id.text_gender_professor);
        birthDateView = findViewById(R.id.text_birth_date);
        emailTextView = findViewById(R.id.text_email);
        facebookView = findViewById(R.id.facebook_view);
        skypeView = findViewById(R.id.skype_view);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        isCurrentUser = intent.getBooleanExtra("ISUSER", false);
        fetchStudent();

        projectsView.setOnClickListener(v -> {
            Intent intentProjects = new Intent(StudentActivity.this, ProjectListActivity.class);
            intentProjects.putExtra("USERID", id);
            intentProjects.putExtra("USERTYPE", FirebaseHelper.STUDENT);
            startActivity(intentProjects);
        });
    }

    private void fetchStudent() {
        // Pega foto do usuário
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
                Log.e("PROFILE_STUDENT_ERR", "IMAGE LOAD ERR--> " + databaseError.getMessage());
            }
        });
        // Pega perfil do usuário
        FirebaseHelper.getStudentRealTime(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        nameTextView.setText(student.getName());
                        genderTextView.setText(student.getGender());
                        birthDateView.setText(student.getBirthDate());
                        emailTextView.setText(student.getEmail());

                        if (!student.getUniversity().isEmpty())
                            getUniversity(student.getUniversity());

                        if (!student.getFacebookUrl().isEmpty()) {
                            facebookView.setBackground(getResources().getDrawable(R.drawable.facebook_logo_blue));
                            facebookView.setOnClickListener(v ->
                                    Helper.openFacebookProfile(student.getFacebookUrl(), StudentActivity.this));
                        } else
                            facebookView.setBackground(getResources().getDrawable(R.drawable.facebook_logo_grey));

                        if (!student.getSkypeUrl().isEmpty()) {
                            skypeView.setBackground(getResources().getDrawable(R.drawable.skype_logo_blue));
                            skypeView.setOnClickListener(v ->
                                    Helper.openSkypeProfile(student.getSkypeUrl(), StudentActivity.this));
                        } else
                            skypeView.setBackground(getResources().getDrawable(R.drawable.skype_logo_grey));
                    }
                } else {
                    Log.e("PROFILE_STUDENT_ERR", "DATA SNAPSHOT DOES NOT EXIST");
                    Toast.makeText(StudentActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROFILE_STUDENT_ERR", databaseError.getMessage());
                Toast.makeText(StudentActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void getUniversity(String university) {
        FirebaseHelper.getUniversityRealTime(university, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    universityView.setText(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UNIVERSITY_STUDENT_ERR", databaseError.getMessage());
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
        if (item.getItemId() == R.id.menu_edit && student != null) {
            Intent intent = new Intent(StudentActivity.this, StudentEditActivity.class);
            intent.putExtra("STUDENT", student);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home)
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