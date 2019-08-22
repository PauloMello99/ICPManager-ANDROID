package br.com.fatec.icpmanager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.dao.StudentDAO;
import br.com.fatec.icpmanager.fragment.UniversitySelectorFragment;
import br.com.fatec.icpmanager.model.Student;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.DatePickerDialogHelper;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class StudentEditActivity extends AppCompatActivity {

    private Student student;
    private EditText nameEditText;
    private EditText universityEditText;
    private EditText birthDateEditText;
    private EditText emailEditText;
    private EditText facebookEditText;
    private EditText skypeEditText;
    private ImageView photoImageView;
    private ImageView editPhotoImageView;
    private Spinner genderSpinner;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit);
        setComponents();
    }

    private void setComponents() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.edit));
        getSupportActionBar().setElevation(0);

        nameEditText = findViewById(R.id.text_name);
        universityEditText = findViewById(R.id.text_university);
        birthDateEditText = findViewById(R.id.text_birth_date);
        emailEditText = findViewById(R.id.text_email);
        facebookEditText = findViewById(R.id.text_facebook);
        skypeEditText = findViewById(R.id.text_skype);
        photoImageView = findViewById(R.id.image_view_photo);
        editPhotoImageView = findViewById(R.id.image_view_edit_holder);
        genderSpinner = findViewById(R.id.spinner_gender);

        setStudent();
        setListeners();
    }

    private void setListeners() {
        editPhotoImageView.setOnClickListener(v -> goToCropActivity());
        photoImageView.setOnClickListener(v -> goToCropActivity());

        universityEditText.setInputType(InputType.TYPE_NULL);
        universityEditText.setOnClickListener(v -> {
            List<String> selectedUniversity = new ArrayList<>();
            selectedUniversity.add(student.getUniversity());
            Intent intent = new Intent(this, UniversitySelectorActivity.class);
            intent.putExtra("USERTYPE", FirebaseHelper.STUDENT);
            intent.putExtra("OPTION", UniversitySelectorFragment.ONE_ONLY);
            intent.putExtra("UNIVERSITIES", (Serializable) selectedUniversity);
            startActivityForResult(intent, 111);
        });

        birthDateEditText.setInputType(InputType.TYPE_NULL);
        DatePickerDialogHelper.setDatePickerDialog(birthDateEditText, this,
                new SimpleDateFormat(getString(R.string.date_format), new Locale("pt", "BR")));
    }

    private void setStudent() {
        Intent intent = getIntent();
        student = (Student) intent.getSerializableExtra("STUDENT");
        if (student.getUniversity() != null) getUniversity(student.getUniversity());

        nameEditText.setText(student.getName());
        birthDateEditText.setText(student.getBirthDate());
        emailEditText.setText(student.getEmail());
        facebookEditText.setText(student.getFacebookUrl());
        skypeEditText.setText(student.getSkypeUrl());

        if (student.getGender().equals(getString(R.string.gender_male)))
            genderSpinner.setSelection(0);
        else if (student.getGender().equals(getString(R.string.gender_female)))
            genderSpinner.setSelection(1);
        else genderSpinner.setSelection(2);

//        // Pega foto
        FirebaseHelper.getPhoto(student.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    if (upload != null && !upload.getPhoto().equals("null"))
                        Picasso.get()
                                .load(upload.getPhoto())
                                .error(R.drawable.error_icon_32)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(photoImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("EDIT_STUDENT_PHOTO_ERR", databaseError.getMessage());
            }
        });
    }

    private void getUniversity(String university) {
        FirebaseHelper.getUniversityRealTime(university, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    universityEditText.setText(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UNIVERSITY_STUDENT_ERR", databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        if (item.getItemId() == R.id.menu_confirm) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.save))
                    .setMessage(getString(R.string.confirm_profile_save))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.confirm, (dialog, which) -> saveStudent());
            builder.create().show();
        }
        return true;
    }

    private void saveStudent() {
        ConstraintLayout layout = findViewById(R.id.layout_student_edit);

        if (nameEditText.getText().toString().trim().isEmpty())
            Snackbar.make(layout, R.string.insert_name, Snackbar.LENGTH_LONG).show();
        else if (emailEditText.getText().toString().trim().isEmpty())
            Snackbar.make(layout, R.string.insert_email, Snackbar.LENGTH_LONG).show();
        else {
            StudentDAO studentDAO = new StudentDAO();
            student.setName(nameEditText.getText().toString().trim());
            student.setBirthDate(birthDateEditText.getText().toString().trim());
            student.setGender(genderSpinner.getSelectedItem().toString());
            student.setFacebookUrl(facebookEditText.getText().toString().trim());
            student.setSkypeUrl(skypeEditText.getText().toString().trim());
            studentDAO.save(student.getId(), student);

            if (fileUri != null && fileUri.getPath() != null) {
                String ext = fileUri.getPath().substring(fileUri.getPath().lastIndexOf("."));
                FirebaseHelper.setPhotoStorage(student.getId(), ext, fileUri)
                        .addOnSuccessListener(taskSnapshot ->
                                FirebaseHelper.getPhotoStorage(student.getId()+ext).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Upload upload = new Upload(uri.toString());
                                    FirebaseHelper.getPhoto(student.getId()).setValue(upload);
                                    finish();
                                }))
                        .addOnFailureListener(e -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.upload_photo_fail))
                                    .setMessage(e.getMessage());
                            builder.create().show();
                        });
            }

            finish();
        }
    }

    private void goToCropActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    555);
        else CropImage.startPickImageActivity(StudentEditActivity.this);
    }

    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            CropImage.startPickImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = CropImage.getPickImageResultUri(this, data);
                    cropRequest(uri);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                        photoImageView.setImageBitmap(bitmap);
                        fileUri = result.getUri();
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileUri = null;
                    }
                }
                break;
            case 111:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String university = data.getStringExtra("UNIVERSITY");
                        student.setUniversity(university);
                        getUniversity(university);
                    }
                }
                break;
        }
    }
}