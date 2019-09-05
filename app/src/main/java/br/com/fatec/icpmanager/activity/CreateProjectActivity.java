package br.com.fatec.icpmanager.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.ProjectPagerAdapter;
import br.com.fatec.icpmanager.dao.ProjectDAO;
import br.com.fatec.icpmanager.dao.UserDAO;
import br.com.fatec.icpmanager.fragment.UniversitySelectorFragment;
import br.com.fatec.icpmanager.fragment.project.FifthPartFragment;
import br.com.fatec.icpmanager.fragment.project.FirstPartFragment;
import br.com.fatec.icpmanager.fragment.project.FourthPartFragment;
import br.com.fatec.icpmanager.fragment.project.ThirdPartFragment;
import br.com.fatec.icpmanager.listener.NewProjectListener;
import br.com.fatec.icpmanager.model.Coordinator;
import br.com.fatec.icpmanager.model.Notification;
import br.com.fatec.icpmanager.model.Phase;
import br.com.fatec.icpmanager.model.Project;
import br.com.fatec.icpmanager.utils.CustomViewPager;
import br.com.fatec.icpmanager.utils.FirebaseHelper;
import br.com.fatec.icpmanager.utils.PreferenceHelper;

public class CreateProjectActivity extends AppCompatActivity
        implements NewProjectListener {

    StepView stepView;
    CustomViewPager viewPager;

    private Project project;
    private ProjectPagerAdapter adapter;
    private ThirdPartFragment thirdPartFragment;
    private FifthPartFragment fifthPartFragment;
    private UserDAO userDAO;
    private int currentStep = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        setComponents();
        stepAction();
    }

        private void stepAction(){

            //FIXME ANIMAÇÃO DO STEP ESTÁ MUITO LENTA (PODE SER O EMULADOR)
            //TODO ADICIONAR TEXTO UNICO PARA CADA FASE
            stepView.setOnStepClickListener(step -> Toast.makeText(CreateProjectActivity.this, "Step " + step, Toast.LENGTH_SHORT).show());
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
    private void setComponents() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.create_project));

        viewPager = findViewById(R.id.newproject_view_pager);
        stepView = findViewById(R.id.step_view);

        project = new Project();
        adapter = new ProjectPagerAdapter(getSupportFragmentManager());
        userDAO = new UserDAO();

        FirstPartFragment firstPartFragment = new FirstPartFragment(this, this);
        UniversitySelectorFragment secondPartFragment =
                new UniversitySelectorFragment(this, this, UniversitySelectorFragment.MANY);
        secondPartFragment.setTitleVisibility(true);
        thirdPartFragment = new ThirdPartFragment(this, this);
        FourthPartFragment fourthPartFragment = new FourthPartFragment(this, this);
        fifthPartFragment = new FifthPartFragment(this, this);

        adapter.addFragment(firstPartFragment);
        adapter.addFragment(secondPartFragment);
        adapter.addFragment(thirdPartFragment);
        adapter.addFragment(fourthPartFragment);
        adapter.addFragment(fifthPartFragment);

        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void checkItemPosition(boolean dir) {
        if (viewPager.getCurrentItem() >= 0 && viewPager.getCurrentItem() < 5)
            if (dir)
                if (adapter.getItem(getItem(0)).isFilled()) viewPager.setCurrentItem(getItem(+1));
                else Toast.makeText(this,
                        getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            else viewPager.setCurrentItem(getItem(-1), true);
    }

    @Override
    public void onProjectInfoFilled() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.preparing_for_project));
        createProject(dialog);
        finish();
    }

    private void createProject(ProgressDialog dialog) {
        PreferenceHelper helper = new PreferenceHelper(this);
        ProjectDAO projectDAO = new ProjectDAO();
        String id = projectDAO.newKey();
        project.setId(id);
        sendMembersNotification();
        clearPendingData();
        if (helper.getUserType().equals("3")) project.setEnable(true);
        else buildCoordinatorNotification();
        projectDAO.save(id, project);
        dialog.dismiss();
    }

    private void clearPendingData() {
        List<String> clear = new ArrayList<>();
        clear.add("");
        project.setProfessors(clear);
        project.setStudents(clear);
    }

    @Override
    public void onPartFilled(int part, Bundle bundle) {
        switch (part) {
            case 1:
                project.setTitle(bundle.getString("TITLE"));
                project.setDescription(bundle.getString("DESC"));
                project.setStartDate(bundle.getString("START"));
                project.setEndDate(bundle.getString("END"));
                break;
            case 2:
                project.setUniversities(bundle.getStringArrayList("UNIVERSITIES"));
                thirdPartFragment.onUniversitiesSeletected(project.getUniversities());
                fifthPartFragment.onUniversitiesSeletected(project.getUniversities());
                break;
            case 3:
                project.setProfessors(bundle.getStringArrayList("PROFESSORS"));
                break;
            case 4:
                project.setPhases((List<Phase>) bundle.getSerializable("PHASES"));
                break;
            case 5:
                project.setStudents(bundle.getStringArrayList("STUDENTS"));
                break;
            default:
                Log.e("ERROR_PART_FILLED", "PART INT DOES NOT EXIST");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
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

    private void buildCoordinatorNotification(){
        String MESSAGE = "O projeto " + project.getTitle() + " solicita sua validação para iniciar";
        String TITLE = getString(R.string.pending_project);
        JSONObject notificationJSON = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notificationJSON.put("to", getString(R.string.TOPIC_USER_APP));
            data.put("title", TITLE);
            data.put("body", MESSAGE);
            data.put("flag","proval");
            notificationJSON.put("data", data);
            FirebaseHelper.getCoordinators(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Coordinator coordinator = snapshot.getValue(Coordinator.class);
                            if (coordinator != null) {
                                Notification notification = new Notification(project.getId(), coordinator.getId(), TITLE, MESSAGE, "proval", false);
                                sendNotification(getString(R.string.NOTIFICATION_URL), notificationJSON, notification);
                            }
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("COORD_SEND_NOTIFI_ERR",databaseError.getMessage());
                    Toast.makeText(CreateProjectActivity.this, "Erro ao enviar notificação para coordenadores", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("ERR_SEND_NOTIFICATION", e.getMessage());
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMembersNotification() {
        String MESSAGE = getString(R.string.invite_user_project) + project.getTitle();
        String TITLE = getString(R.string.new_project);
        for (String student : project.getStudents())
            buildNotification(student, MESSAGE, TITLE);
        for (String professor : project.getProfessors())
            buildNotification(professor, MESSAGE, TITLE);
    }

    private void buildNotification(String UID, String MESSAGE, String TITLE) {
        JSONObject notificationJSON = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notificationJSON.put("to", getString(R.string.TOPIC_USER_APP));
            data.put("to", UID);
            data.put("title", TITLE);
            data.put("body", MESSAGE);
            data.put("flag","npro");
            notificationJSON.put("data", data);
            Notification notification = new Notification(project.getId(), UID, TITLE, MESSAGE, "npro", false);
            sendNotification(getString(R.string.NOTIFICATION_URL), notificationJSON, notification);
        } catch (Exception e) {
            Log.e("ERR_SEND_NOTIFICATION", e.getMessage());
            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String notification_url, JSONObject notificationJSON, Notification notification) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, notification_url,
                notificationJSON, response -> {
        }, error ->
                Log.e("SEND_MESSAGE_ERR", (error.getMessage()==null ? "ERRO AO ENVIAR MENSAGEM":error.getMessage()))) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", getString(R.string.NOTIFICATION_SERVER_KEY));
                header.put("ContentType", "application/json");
                return header;
            }
        };
        requestQueue.add(objectRequest);
        userDAO.addNotification(notification.getTo(), notification);
    }

}