package br.com.fatec.icpmanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.activity.ProjectActivity;
import br.com.fatec.icpmanager.adapter.NotificationAdapter;
import br.com.fatec.icpmanager.dao.ProjectDAO;
import br.com.fatec.icpmanager.dao.UserDAO;
import br.com.fatec.icpmanager.listener.NotificationListener;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.model.Notification;
import br.com.fatec.icpmanager.utils.FirebaseHelper;

public class NotificationsFragment extends Fragment implements RecyclerViewClickListener {

    private View view;
    private Context context;

    private LottieAnimationView emptyListView;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private NotificationListener listener;

    private int type;
    private String id;
    private UserDAO userDAO;
    private ProjectDAO projectDAO;
    private int readCount;

    public NotificationsFragment(){ }

    public NotificationsFragment(int type, Context context, NotificationListener listener) {
        this.context = context;
        this.type = type;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        setComponents();
        return view;
    }

    private void setComponents() {
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this, context);
        recyclerView = view.findViewById(R.id.recycler_notifications);
        emptyListView = view.findViewById(R.id.animation_empty_list);

        id = FirebaseHelper.getCurrentUser().getUid();
        userDAO = new UserDAO();
        projectDAO = new ProjectDAO();
        getNotifications();
        setRecycler();
    }

    private void getNotifications() {
        FirebaseHelper.getNotifications(FirebaseHelper.getCurrentUser().getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    notificationList.clear();
                    readCount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        if (notification != null &&
                                notification.getTo()
                                        .equals(FirebaseHelper.getCurrentUser().getUid())) {
                            notificationList.add(notification);
                            if (!notification.isRead()) readCount++;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    setResponseLayout(notificationList.size(), readCount);
                } else
                    setResponseLayout(0, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NOTIFICATIONS_ERR", databaseError.getMessage());
                Toast.makeText(context, getString(R.string.error_load_notifications), Toast.LENGTH_SHORT).show();
                setResponseLayout(-1, 0);
            }
        });
    }

    private void setRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setResponseLayout(int size, int readCount) {
        listener.onNotificationCountChanged(readCount);
        if (size > 0) {
            emptyListView.pauseAnimation();
            emptyListView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else if (size == 0) {
            emptyListView.setAnimation("no_items_search.json");
            emptyListView.playAnimation();
            recyclerView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setAnimation("error.json");
            emptyListView.playAnimation();
            recyclerView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void recyclerViewListClicked(View v, int position, int TAG, String TYPE) {
        if (TAG == NotificationAdapter.READ) {
            userDAO.markNotificationRead(id, notificationList.get(position).getId());
            listener.onNotificationCountChanged(readCount--);
            Intent intent = new Intent(context, ProjectActivity.class);
            intent.putExtra("PROJECT_ID", notificationList.get(position).getProjectId());
            startActivity(intent);
        } else if (TAG == NotificationAdapter.ACCEPT) {
            if (notificationList.get(position).getFlag().equals("proval"))
                projectDAO.enableProject(notificationList.get(position).getProjectId());
            else projectDAO.addUser(notificationList.get(position).getProjectId(), id, type);
            userDAO.removeNotification(id, notificationList.get(position).getId());
        } else userDAO.removeNotification(id, notificationList.get(position).getId());
    }
}