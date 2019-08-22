package br.com.fatec.icpmanager.fragment.project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.adapter.CreatePhaseAdapter;
import br.com.fatec.icpmanager.listener.NewProjectListener;
import br.com.fatec.icpmanager.model.Phase;
import br.com.fatec.icpmanager.utils.DatePickerDialogHelper;

@SuppressLint("ValidFragment")
public class FourthPartFragment extends CustomFragment {

    private CreatePhaseAdapter adapter;
    private List<Phase> phases;

    public FourthPartFragment(NewProjectListener listener, Context context) {
        super(listener, context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newproject_phases, container, false);
        setComponents();
        setRecycler();
        return view;
    }

    private void setRecycler() {
        RecyclerView phasesRecyclerView = view.findViewById(R.id.recycler_phases);
        phasesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        phasesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setComponents() {
        super.setComponents();
        FloatingActionButton button = view.findViewById(R.id.fab_Add);
        button.setOnClickListener(v -> openPhaseDialog(-1, "NEW"));

        phases = new ArrayList<>();
        adapter = new CreatePhaseAdapter(phases, context);
    }


    @Override
    public void savePart() {
        if (phases.size() > 0) {
            setFilled(true);
            bundle.clear();
            bundle.putSerializable("PHASES", (Serializable) phases);
            listener.onPartFilled(4, bundle);
        } else
            setFilled(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.confirm_phase_delete))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                        phases.remove(item.getGroupId());
                        adapter.notifyItemRemoved(item.getGroupId());
                    });
            builder.create().show();
        } else if (item.getItemId() == 2) openPhaseDialog(item.getGroupId(), "EDIT");
        return super.onContextItemSelected(item);
    }

    private void openPhaseDialog(int position, String TAG) {
        View view = getLayoutInflater().inflate(R.layout.fragment_new_phase, null);
        EditText titleTextView = view.findViewById(R.id.title_phase);
        EditText descTextView = view.findViewById(R.id.description_phase);
        EditText dateTextView = view.findViewById(R.id.endDate_phase);

        Locale locale = new Locale("pt", "BR");
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format), locale);

        dateTextView.setInputType(InputType.TYPE_NULL);
        DatePickerDialogHelper.setDatePickerDialog(dateTextView, context, sdf);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        if (TAG.equals("NEW")) {
            builder.setTitle(getString(R.string.new_phase))
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        if (checkFields(titleTextView, descTextView, dateTextView)) {
                            Phase phase = new Phase(titleTextView.getText().toString(),
                                    descTextView.getText().toString(),
                                    dateTextView.getText().toString());

                            phases.add(phase);
                            adapter.notifyDataSetChanged();
                            savePart();
                        } else
                            Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                    });
        } else if (TAG.equals("EDIT")) {
            titleTextView.setText(phases.get(position).getTitle());
            descTextView.setText(phases.get(position).getDescription());
            dateTextView.setText(phases.get(position).getEndDate());

            builder.setTitle(getString(R.string.edit));
            builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                if (checkFields(titleTextView, descTextView, dateTextView)) {
                    phases.get(position).setTitle(titleTextView.getText().toString());
                    phases.get(position).setDescription(descTextView.getText().toString());
                    phases.get(position).setEndDate(dateTextView.getText().toString());
                    adapter.notifyItemChanged(position);
                    savePart();
                } else
                    Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            });
        }

        builder.create().show();
    }

    private boolean checkFields(EditText editText1, EditText editText2, EditText editText3) {
        return !editText1.getText().toString().trim().isEmpty() &&
                !editText2.getText().toString().trim().isEmpty() &&
                !editText3.getText().toString().trim().isEmpty();
    }
}
