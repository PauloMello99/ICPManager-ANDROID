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
import android.widget.TextView;

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
import java.util.List;
import java.util.Locale;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.dao.CoordinatorDAO;
import br.com.fatec.icpmanager.fragment.UniversitySelectorFragment;
import br.com.fatec.icpmanager.model.Coordinator;
import br.com.fatec.icpmanager.model.University;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.DatePickerDialogHelper;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class CoordinatorEditActivity extends AppCompatActivity {

    private Coordinator coordinator;
    private EditText nameEditText;
    private TextView universityEditText;
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
        setContentView(R.layout.activity_professor_edit);
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

        setCoordinator();
        setListeners();
    }

    private void setCoordinator() {
        Intent intent = getIntent();
        coordinator = (Coordinator) intent.getSerializableExtra("COORDINATOR");
        if (coordinator.getUniversityList() != null) getUniversity(coordinator.getUniversityList());

        nameEditText.setText(coordinator.getName());
        birthDateEditText.setText(coordinator.getBirthDate());
        emailEditText.setText(coordinator.getEmail());
        facebookEditText.setText(coordinator.getFacebookUrl());
        skypeEditText.setText(coordinator.getSkypeUrl());

        if (coordinator.getGender().equals(getString(R.string.gender_male)))
            genderSpinner.setSelection(0);
        else if (coordinator.getGender().equals(getString(R.string.gender_female)))
            genderSpinner.setSelection(1);
        else genderSpinner.setSelection(2);

        // Pega foto
        FirebaseHelper.getPhoto(coordinator.getId(), new ValueEventListener() {
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
                Log.e("EDIT_COORD_PHOTO_ERR", databaseError.getMessage());
            }
        });
    }

    private void setListeners() {
        editPhotoImageView.setOnClickListener(v -> goToCropActivity());
        photoImageView.setOnClickListener(v -> goToCropActivity());

        universityEditText.setOnClickListener(v -> {
            List<String> selectedUniversities = coordinator.getUniversityList();

            Intent intent = new Intent(this, UniversitySelectorActivity.class);
            intent.putExtra("USERTYPE", FirebaseHelper.COORDINATOR);
            intent.putExtra("OPTION", UniversitySelectorFragment.MANY);
            intent.putExtra("UNIVERSITIES", (Serializable) selectedUniversities);
            startActivityForResult(intent, 111);
        });

        birthDateEditText.setInputType(InputType.TYPE_NULL);
        DatePickerDialogHelper.setDatePickerDialog(birthDateEditText, this,
                new SimpleDateFormat(getString(R.string.date_format), new Locale("pt", "BR")));
    }

    private void getUniversity(List<String> universityList) {
        FirebaseHelper.getUniversitiesRealTime(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String text = "";
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        University university = snapshot.getValue(University.class);
                        if (university != null && universityList.contains(university.getId()))
                            text = text.concat(university.getName().concat("\n"));
                    }
                    universityEditText.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UNIVERSITY_PROF_ERR", databaseError.getMessage());
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
                    .setPositiveButton(R.string.confirm, (dialog, which) -> saveCoordinator());
            builder.create().show();
        }
        return true;
    }

    private void saveCoordinator() {
        ConstraintLayout layout = findViewById(R.id.layout_professor_edit);

        if (nameEditText.getText().toString().trim().isEmpty())
            Snackbar.make(layout, R.string.insert_name, Snackbar.LENGTH_LONG).show();
        else if (emailEditText.getText().toString().trim().isEmpty())
            Snackbar.make(layout, R.string.insert_email, Snackbar.LENGTH_LONG).show();
        else {
            CoordinatorDAO coordinatorDAO = new CoordinatorDAO();
            coordinator.setName(nameEditText.getText().toString().trim());
            coordinator.setBirthDate(birthDateEditText.getText().toString().trim());
            coordinator.setGender(genderSpinner.getSelectedItem().toString());
            coordinator.setFacebookUrl(facebookEditText.getText().toString().trim());
            coordinator.setSkypeUrl(skypeEditText.getText().toString().trim());
            coordinatorDAO.save(coordinator.getId(), coordinator);

            if (fileUri != null && fileUri.getPath() != null) {
                String ext = fileUri.getPath().substring(fileUri.getPath().lastIndexOf("."));
                FirebaseHelper.setPhotoStorage(coordinator.getId(), ext, fileUri)
                        .addOnSuccessListener(taskSnapshot ->
                                FirebaseHelper.getPhotoStorage(coordinator.getId() + ext).getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Upload upload = new Upload(uri.toString());
                                            FirebaseHelper.getPhoto(coordinator.getId()).setValue(upload);
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
        else CropImage.startPickImageActivity(CoordinatorEditActivity.this);
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
                        Log.e("ERROR_PHOTO", e.getMessage());
                        fileUri = null;
                    }
                }
                break;
            case 111:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        List<String> university = (List<String>) data.getSerializableExtra("UNIVERSITY");
                        coordinator.setUniversityList(university);
                        getUniversity(university);
                    }
                }
                break;
        }
    }

}

