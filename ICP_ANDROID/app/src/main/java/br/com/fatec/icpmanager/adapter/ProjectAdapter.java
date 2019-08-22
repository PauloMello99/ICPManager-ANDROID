package br.com.fatec.icpmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.activity.ProjectActivity;
import br.com.fatec.icpmanager.model.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<Project> projects;
    private Context context;

    public ProjectAdapter(List<Project> projects, Context context) {
        this.projects = projects;
        this.context = context;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_project, viewGroup, false);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder viewHolder, int i) {
        final Project project = projects.get(i);
        viewHolder.nameTextView.setText(project.getTitle());
        viewHolder.infoTextView.setText(project.getDescription());
        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProjectActivity.class);
            intent.putExtra("PROJECT_ID", project.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;
        TextView nameTextView;
        TextView infoTextView;

        ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.card_photo);
            nameTextView = itemView.findViewById(R.id.card_title);
            infoTextView = itemView.findViewById(R.id.card_desc);
        }
    }
}