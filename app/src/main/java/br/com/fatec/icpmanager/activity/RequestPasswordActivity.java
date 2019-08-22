package br.com.fatec.icpmanager.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.utils.Helper;

public class RequestPasswordActivity extends AppCompatActivity {

    private ImageView backView;
    private TextInputLayout emailLayout;
    private EditText emailEditText;
    private Button sendButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_password);
        setComponents();
    }

    private void setComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        backView = findViewById(R.id.back_button);
        emailEditText = findViewById(R.id.reset_edit_text);
        emailLayout = findViewById(R.id.input_layout_password_reset);
        sendButton = findViewById(R.id.reset_password_button);

        backView.setOnClickListener(v -> finish());
        sendButton.setOnClickListener(v -> sendEmail());
    }

    private void sendEmail() {
        String email = emailEditText.getText().toString().trim();
        if (validateField(email))
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            finish();
                        else {
                            Log.e("RESET_PASSWD_ERROR", task.getException().toString());
                            Toast.makeText(RequestPasswordActivity.this,
                                    getString(R.string.error_get_data), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private boolean validateField(String email) {
        if (Helper.isEmpty(email))
            emailLayout.setError(getString(R.string.insert_email));

        return !Helper.isEmpty(email);
    }
}
