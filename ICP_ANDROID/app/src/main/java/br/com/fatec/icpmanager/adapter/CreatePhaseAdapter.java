package br.com.fatec.icpmanager.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.model.Phase;

public class CreatePhaseAdapter extends RecyclerView.Adapter<CreatePhaseAdapter.PhaseViewHolder> {

    private Context context;
    private List<Phase> phases;

    public CreatePhaseAdapter(List<Phase> phases, Context context) {
        this.phases = phases;
        this.context = context;
    }

    @NonNull
    @Override
    public PhaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_new_phase, parent, false);
        return new PhaseViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull PhaseViewHolder holder, int position) {
        Phase phase = phases.get(position);

        holder.titleTextView.setText(phase.getTitle());
        holder.descTextView.setText(phase.getDescription());
        holder.dateTextView.setText(phase.getEndDate());
    }

    @Override
    public int getItemCount() {
        return phases.size();
    }

    static class PhaseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView titleTextView;
        TextView descTextView;
        TextView dateTextView;
        Context context;

        PhaseViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            titleTextView = itemView.findViewById(R.id.title_phase);
            descTextView = itemView.findViewById(R.id.description_phase);
            dateTextView = itemView.findViewById(R.id.endDate_phase);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(context.getString(R.string.options));
            menu.add(getAdapterPosition(), 1, Menu.NONE, context.getString(R.string.delete));
            menu.add(getAdapterPosition(), 2, Menu.NONE, context.getString(R.string.edit));
        }
    }
}
