package br.com.fatec.icpmanager.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.dao.ProfessorDAO;
import br.com.fatec.icpmanager.dao.StudentDAO;
import br.com.fatec.icpmanager.dao.UserDAO;
import br.com.fatec.icpmanager.model.Professor;
import br.com.fatec.icpmanager.model.Student;
import br.com.fatec.icpmanager.model.User;
import br.com.fatec.icpmanager.utils.Helper;
import br.com.fatec.icpmanager.utils.PreferenceHelper;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout passwordConfirmLayout;
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText passwordConfirmEditText;
    private Spinner userTypeSpinner;
    private Button createButton;
    private LottieAnimationView animationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setComponents();
    }

    private void setComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        ImageView backView = findViewById(R.id.back_button);
        nameLayout = findViewById(R.id.input_layout_name);
        emailLayout = findViewById(R.id.input_layout_email);
        passwordLayout = findViewById(R.id.input_layout_password);
        passwordConfirmLayout = findViewById(R.id.input_layout_password_confirm);
        nameEditText = findViewById(R.id.edit_text_name);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        passwordConfirmEditText = findViewById(R.id.edit_text_password_confirm);
        userTypeSpinner = findViewById(R.id.spinner_user_type);
        createButton = findViewById(R.id.button_create);
        animationView = findViewById(R.id.lottie_loading);

        backView.setOnClickListener(v -> finish());
        createButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirm = passwordConfirmEditText.getText().toString().trim();
        if (validateFields(name, email, password, passwordConfirm)) {
            createButton.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String id = firebaseUser.getUid();
                            String type = userTypeSpinner.getSelectedItem().toString();
                            createUser(id, type, email, name);
                        } else {
                            createButton.setVisibility(View.VISIBLE);
                            animationView.setVisibility(View.GONE);
                            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createUser(String id, String type, String email, String name) {
        PreferenceHelper helper = new PreferenceHelper(this);
        User user = new User(id, type,helper.getMessagingToken());
        UserDAO userDAO = new UserDAO();
        userDAO.save(id, user);
        if (type.equals(getString(R.string.student))) {
            Student student = new Student(id, name, email, true);
            StudentDAO studentDAO = new StudentDAO();
            studentDAO.save(id, student);
        } else if (type.equals(getString(R.string.professor))) {
            Professor professor = new Professor(id, name, email, true);
            ProfessorDAO professorDAO = new ProfessorDAO();
            professorDAO.save(id, professor);
        }
        createButton.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.sucessful_account_creation), Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateFields(String name, String email, String password, String passwordConfirm) {
        boolean check = true;
        if (Helper.isEmpty(name)){
            nameLayout.setError(getString(R.string.insert_name));
            check = false;
        }
        if (Helper.isEmpty(email)){
            emailLayout.setError(getString(R.string.insert_email));
            check = false;
        }
        if (Helper.isEmpty(password)){
            passwordLayout.setError(getString(R.string.insert_password));
            check = false;
        }
        if (Helper.isEmpty(passwordConfirm)){
            passwordConfirmLayout.setError(getString(R.string.insert_password));
            check = false;
        }

        return check;
    }
}