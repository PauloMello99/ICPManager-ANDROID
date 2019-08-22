package br.com.fatec.icpmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.dao.UserDAO;
import br.com.fatec.icpmanager.model.User;
import br.com.fatec.icpmanager.utils.FirebaseHelper;
import br.com.fatec.icpmanager.utils.PreferenceHelper;

public class SplashActivity extends AppCompatActivity implements Runnable {

    private PreferenceHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new PreferenceHelper(this);
        if (helper.getNightMode().equals("true"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(this, 1000);
    }

    @Override
    public void run() {
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.TOPIC_USER_APP));
        //TODO SUBSCRIBE TO USER ID FOR MESSAGE
        if (FirebaseHelper.getCurrentUser() != null) goToMain();
        else goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    private void goToMain() {
        FirebaseHelper.getUser(FirebaseHelper.getCurrentUser().getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) getUserType(user);
                        else goToLogin();
                    }
                } catch (Exception e) {
                    Log.e("GET_USER_ERROR", e.getMessage());
                    goToLogin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SPLASH_USER_FAIL", databaseError.getMessage());
                Toast.makeText(SplashActivity.this,
                        getString(R.string.login_failure), Toast.LENGTH_SHORT).show();
                goToLogin();
            }
        });
    }

    private void getUserType(User user) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        switch (user.getType()) {
            case "Estudante":
                helper.putUserType(1);
                break;
            case "Professor":
                helper.putUserType(2);
                break;
            case "Coordenador":
                helper.putUserType(3);
                FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.TOPIC_COORDINATOR_APP));
                break;
        }
        startActivity(intent);
        finish();
    }
}