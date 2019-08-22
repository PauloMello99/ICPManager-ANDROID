package br.com.fatec.icpmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.fatec.icpmanager.R;

public class PolicyPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_privacy);

        ImageView backView = findViewById(R.id.back_button);
        backView.setOnClickListener(v -> finish());
    }
}
