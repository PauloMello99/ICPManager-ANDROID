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
import br.com.fatec.icpmanager.model.University;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private int selectedOption = -1;
    public static int FILTER = 0;
    public static int REMOVE = 1;

    private List<University> universities;
    private Context context;
    private RecyclerViewClickListener itemListener;

    public UniversityAdapter(List<University> universities,
                             Context context, RecyclerViewClickListener itemListener,
                             int TAG) {
        this.universities = universities;
        this.context = context;
        this.itemListener = itemListener;
        if (TAG == 1 || TAG == 0) selectedOption = TAG;
    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        switch (selectedOption) {
            case 0:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.card_project, viewGroup, false);
                break;
            case 1:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.card_round, viewGroup, false);
                break;
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.card_project, viewGroup, false);
                break;
        }

        return new UniversityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UniversityViewHolder viewHolder, int i) {
        final University university = universities.get(i);
        if (selectedOption == REMOVE) viewHolder.nameTextView.setText(university.getInitials());
        else {
            viewHolder.nameTextView.setText(university.getName());
            viewHolder.infoTextView.setText(university.getDepartment());
        }
        if (itemListener != null)
            viewHolder.itemView.setOnClickListener(v -> itemListener.recyclerViewListClicked(v, i, selectedOption, "UNIVERSITY"));
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    static class UniversityViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;
        TextView nameTextView;
        TextView infoTextView;

        UniversityViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.card_photo);
            nameTextView = itemView.findViewById(R.id.card_title);
            infoTextView = itemView.findViewById(R.id.card_desc);
        }
    }
}