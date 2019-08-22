package br.com.fatec.icpmanager.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.ViewPagerAdapter;
import br.com.fatec.icpmanager.dao.UserDAO;
import br.com.fatec.icpmanager.fragment.FeedFragment;
import br.com.fatec.icpmanager.fragment.NotificationsFragment;
import br.com.fatec.icpmanager.fragment.ProjectsFragment;
import br.com.fatec.icpmanager.fragment.SearchFragment;
import br.com.fatec.icpmanager.listener.NotificationListener;
import br.com.fatec.icpmanager.model.Upload;
import br.com.fatec.icpmanager.utils.FirebaseHelper;
import br.com.fatec.icpmanager.utils.Helper;
import br.com.fatec.icpmanager.utils.PreferenceHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NotificationListener {

    private ViewPager viewPager;
    private AHBottomNavigation bottomNavView;
    private DrawerLayout drawerLayout;
    private ConstraintLayout contentLayout;
    private NavigationView navView;
    private String id;
    private int type;
    private PreferenceHelper helper;
    private boolean notification;
    private int notification_id;

    private void reloadActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new PreferenceHelper(this);
        if (helper.getNightMode().equals("true"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        getInfo();
        setComponents();
        setNavView();
    }

    private void setComponents() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavView = findViewById(R.id.bottomNavigationView);
        navView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        contentLayout = findViewById(R.id.content_main);
        configBottomNavView();

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FeedFragment(), getString(R.string.feed));
        pagerAdapter.addFragment(new SearchFragment(this), getString(R.string.search));
        pagerAdapter.addFragment(new ProjectsFragment(this, type), getString(R.string.projects));
        pagerAdapter.addFragment(new NotificationsFragment(type,this,this), getString(R.string.notifications));
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavView.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (notification) goToNotifications();
        setDrawer();
    }

    private void configBottomNavView() {
        bottomNavView.addItem(new AHBottomNavigationItem(R.string.feed,R.drawable.ic_dashboard_white,R.color.colorAccent));
        bottomNavView.addItem(new AHBottomNavigationItem(R.string.search,R.drawable.ic_search_white,R.color.colorAccent));
        bottomNavView.addItem(new AHBottomNavigationItem(R.string.projects,R.drawable.ic_projects_white,R.color.colorAccent));
        bottomNavView.addItem(new AHBottomNavigationItem(R.string.notifications,R.drawable.ic_notifications_white,R.color.colorAccent));
        bottomNavView.addItem(new AHBottomNavigationItem(R.string.configurations,R.drawable.ic_format_list_white,R.color.colorAccent));
        bottomNavView.setDefaultBackgroundColor(getResources().getColor(R.color.colorBackground));
        bottomNavView.setAccentColor(getResources().getColor(R.color.colorAccent));
        bottomNavView.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavView.setNotificationBackgroundColor(getResources().getColor(R.color.colorRed));
        bottomNavView.setOnTabSelectedListener((position, wasSelected) -> {
            if(position!=4)viewPager.setCurrentItem(position);
            else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        });
    }

    private void getInfo() {
        Intent intent = getIntent();
        type = Integer.parseInt(helper.getUserType());
        id = FirebaseHelper.getCurrentUser().getUid();
        UserDAO userDAO = new UserDAO();
        userDAO.setMessageToken(id,helper.getMessagingToken());
        notification = intent.getBooleanExtra("NOTIFICATION", false);
        notification_id = intent.getIntExtra("NOTIFICATION_ID",-1);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        notification_id = intent.getIntExtra("NOTIFICATION_ID",-1);
        goToNotifications();
    }

    private void goToNotifications() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notification_id);
        viewPager.setCurrentItem(3, true);
        bottomNavView.setCurrentItem(3);
    }

    private void setNavView() {
        View header = navView.getHeaderView(0);
        TextView name = header.findViewById(R.id.nav_name);
        CircleImageView photo = header.findViewById(R.id.nav_photo);
        TextView typeView = header.findViewById(R.id.nav_type);

        MenuItem darkMode = navView.getMenu().getItem(1);
        MenuItem notification = navView.getMenu().getItem(2);
        SwitchCompat themeSwitchCompat = new SwitchCompat(this);
        SwitchCompat themeSwitchCompat1 = new SwitchCompat(this);
        MenuItemCompat.setActionView(darkMode, themeSwitchCompat);
        MenuItemCompat.setActionView(notification, themeSwitchCompat1);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            themeSwitchCompat.setChecked(true);
        themeSwitchCompat.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    helper.putNightMode(isChecked);
                    reloadActivity();
                });

        if(helper.getNotification() == null) helper.putNotification(false);
        else if(helper.getNotification().equals("true"))themeSwitchCompat1.setChecked(true);
        themeSwitchCompat1.setOnCheckedChangeListener(
                (buttonView, isChecked) -> helper.putNotification(isChecked));

        navView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_profile:
                    Intent intentProfile;
                    switch (type){
                        case 1:
                            intentProfile = new Intent(MainActivity.this, StudentActivity.class);
                            break;
                        case 2:
                            intentProfile = new Intent(MainActivity.this, ProfessorActivity.class);
                            break;
                        case 3:
                            intentProfile = new Intent(MainActivity.this, CoordinatorActivity.class);
                            break;
                        default:
                            intentProfile = null;
                            break;
                    }
                    if(intentProfile!=null){
                        intentProfile.putExtra("ID", id);
                        intentProfile.putExtra("ISUSER", true);
                        startActivity(intentProfile);
                    }
                    break;
                case R.id.menu_about:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
                case R.id.menu_exit:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    break;
            }
            return false;
        });

        switch (type) {
            case 1:
                typeView.setText(getString(R.string.student));
                FirebaseHelper.getStudentRealTime(id, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.child("name").exists())
                            name.setText(Helper.cutName(
                                    Objects.requireNonNull(dataSnapshot.child("name")
                                            .getValue(String.class)), 0, 2));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("STUDENT_NAV_ERR", databaseError.getMessage());
                    }
                });
                break;
            case 2:
                typeView.setText(getString(R.string.professor));
                FirebaseHelper.getProfessorRealTime(id, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            name.setText(Helper.cutName(
                                    Objects.requireNonNull(dataSnapshot.child("name")
                                            .getValue(String.class)), 0, 2));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PROFESSOR_NAV_ERR", databaseError.getMessage());
                    }
                });
                break;
            case 3:
                typeView.setText(getString(R.string.coordinator));
                FirebaseHelper.getCoordinatorRealTime(id, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            name.setText(Helper.cutName(
                                    Objects.requireNonNull(dataSnapshot.child("name")
                                            .getValue(String.class)), 0, 2));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("COORDINATOR_NAV_ERR", databaseError.getMessage());
                    }
                });
                break;
        }

        FirebaseHelper.getPhotoRealTime(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    if (upload != null && !upload.getPhoto().equals("null"))
                        Picasso.get()
                                .load(upload.getPhoto())
                                .error(R.drawable.error_icon_32)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PROFILE_STUDENT_ERR", "IMAGE LOAD ERR--> " + databaseError.getMessage());
            }
        });
    }

    private void setDrawer() {
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        float slideX = drawerView.getWidth() * -slideOffset;
                        contentLayout.setTranslationX(slideX);
                    }
                };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onNotificationCountChanged(int count) {
        if(count!=0) bottomNavView.setNotification(String.valueOf(count),3);
        else bottomNavView.setNotification("",3);
    }
}