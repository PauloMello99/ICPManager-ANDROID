package br.com.fatec.icpmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.model.User;
import br.com.fatec.icpmanager.utils.FirebaseHelper;
import br.com.fatec.icpmanager.utils.Helper;
import br.com.fatec.icpmanager.utils.PreferenceHelper;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout loginLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText loginEditText;
    private TextInputEditText passwordEditText;
    private FirebaseAuth auth;
    private Button loginButton;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceHelper helper = new PreferenceHelper(this);
        if (helper.getNightMode().equals("true"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_login);
        setComponents();
    }

    private void setComponents() {
        loginLayout = findViewById(R.id.input_layout_login);
        passwordLayout = findViewById(R.id.input_layout_password);
        loginEditText = findViewById(R.id.edit_text_login);
        passwordEditText = findViewById(R.id.edit_text_password);
        TextView forgotPassword = findViewById(R.id.text_forgot_password);
        TextView createAccount = findViewById(R.id.text_create_account);
        TextView policyPrivacy = findViewById(R.id.text_policy_privacy);
        loginButton = findViewById(R.id.button_login);
        animationView = findViewById(R.id.lottie_loading);
        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> login());

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null &&
                    (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE) ||
                    (actionId == EditorInfo.IME_ACTION_NEXT))
                login();
            return false;
        });

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RequestPasswordActivity.class)));

        policyPrivacy.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, PolicyPrivacyActivity.class)));

        createAccount.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
    }

    private boolean validateFields(String login, String password) {
        if (Helper.isEmpty(login))
            loginLayout.setError(getString(R.string.insert_email));
        if (Helper.isEmpty(password))
            passwordLayout.setError(getString(R.string.insert_password));
        return !Helper.isEmpty(login) && !Helper.isEmpty(password);
    }

    private void login() {
        hideKeyboard();
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (validateFields(login, password)) {
            loginButton.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(login, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful())
                            goToMain();
                        else {
                            animationView.setVisibility(View.GONE);
                            loginButton.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.login_failure), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void goToMain() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseHelper.getUser(firebaseUser.getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) getUserType(user);
                    else {
                        animationView.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.login_failure), Toast.LENGTH_SHORT).show();
                        Log.e("LOGIN_USER_FAIL", "null user");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LOGIN_USER_FAIL", databaseError.getMessage());
                Toast.makeText(LoginActivity.this,
                        getString(R.string.login_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserType(User user) {
        PreferenceHelper helper = new PreferenceHelper(this);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        switch (user.getType()) {
            case "Estudante":
                helper.putUserType(1);
                break;
            case "Professor":
                helper.putUserType(2);
                break;
            case "Coordenador":
                helper.putUserType(3);
                break;
        }
        startActivity(intent);
        finish();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}