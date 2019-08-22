package br.com.fatec.icpmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.listener.RecyclerViewClickListener;
import br.com.fatec.icpmanager.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private RecyclerViewClickListener clickListener;

    public static int ACCEPT = 1;
    public static int REFUSE = 0;
    public static int READ = 2;

    public NotificationAdapter(List<Notification> notificationList,
                               RecyclerViewClickListener clickListener, Context context) {
        this.notificationList = notificationList;
        this.clickListener = clickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.card_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.nameTextView.setText(notification.getTitle());
        holder.infoTextView.setText(notification.getBody());

        holder.itemView.setOnClickListener(v ->
                clickListener.recyclerViewListClicked(v, position, READ, null));

        holder.refuseImageView.setOnClickListener(v ->
                clickListener.recyclerViewListClicked(v, position, REFUSE, null));

        holder.acceptImageView.setOnClickListener(v ->
                clickListener.recyclerViewListClicked(v, position, ACCEPT, null));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView infoTextView;
        ImageView acceptImageView;
        ImageView refuseImageView;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.card_title);
            infoTextView = itemView.findViewById(R.id.card_desc);
            acceptImageView = itemView.findViewById(R.id.card_notification_accept);
            refuseImageView = itemView.findViewById(R.id.card_notification_refuse);
        }
    }
}
