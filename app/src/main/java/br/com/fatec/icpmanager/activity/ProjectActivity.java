package br.com.fatec.icpmanager.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.model.Project;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class ProjectActivity extends AppCompatActivity {

    TextView nameTextView;
    TextView descTextView;
    TextView startTextView;
    TextView endTextView;
    ProgressBar progressBar;
    StepView stepView;

    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setComponents();
        stepAction();
    }


    private void setComponents() {
        getProject();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        nameTextView = findViewById(R.id.project_name);
        descTextView = findViewById(R.id.desc_project);
        startTextView = findViewById(R.id.start_date_project);
        endTextView = findViewById(R.id.end_date_project);
        progressBar = findViewById(R.id.progress_project);
        stepView = findViewById(R.id.step_view);
    }

    private void getProject() {
        String projectId = getIntent().getStringExtra("PROJECT_ID");
        FirebaseHelper.getProject(projectId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Project project = dataSnapshot.getValue(Project.class);
                    if(project!=null){
                        nameTextView.setText(project.getTitle());
                        descTextView.setText(project.getDescription());
                        startTextView.setText(project.getStartDate());
                        endTextView.setText(project.getEndDate());
                    }
                } else{
                    Toast.makeText(ProjectActivity.this, "Falha ao carregar Projeto", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROJECT_ACTIVITY_ERR",databaseError.getMessage());
                Toast.makeText(ProjectActivity.this, "Falha ao carregar Projeto", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void stepAction(){

        //FIXME ANIMAÇÃO DO STEP ESTÁ MUITO LENTA (PODE SER O EMULADOR)
        //TODO ADICIONAR TEXTO UNICO PARA CADA FASE
        stepView.setOnStepClickListener(step -> Toast.makeText(ProjectActivity.this, "Step " + step, Toast.LENGTH_SHORT).show());
        findViewById(R.id.next).setOnClickListener(v -> {
            if (currentStep < stepView.getStepCount() - 1) {
                currentStep++;
                stepView.go(currentStep, true);
            } else {
                stepView.done(true);
            }
        });
        findViewById(R.id.back).setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
            }
            stepView.done(false);
            stepView.go(currentStep, true);
        });
        List<String> steps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            steps.add("Step " + (i + 1));
        }
        steps.set(steps.size() - 1, steps.get(steps.size() - 1) + " last one");
        stepView.setSteps(steps);

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