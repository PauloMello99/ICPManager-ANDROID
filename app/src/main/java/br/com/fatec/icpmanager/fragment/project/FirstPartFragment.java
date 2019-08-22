package br.com.fatec.icpmanager.fragment.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.fatec.icpmanager.R;
import br.com.fatec.icpmanager.activity.CreateProjectActivity;
import br.com.fatec.icpmanager.listener.NewProjectListener;
import br.com.fatec.icpmanager.utils.DatePickerDialogHelper;

@SuppressLint("ValidFragment")
public class FirstPartFragment extends CustomFragment {

    private EditText titleEditText;
    private EditText descEditText;
    private EditText startEditText;
    private EditText endEditText;

    public FirstPartFragment(NewProjectListener listener, Context context) {
        super(listener, context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newproject_text, container, false);
        setComponents();
        setListeners();
        return view;
    }

    private void setListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verifyFields()) setFilled(true);
                else setFilled(false);

                savePart();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        titleEditText.addTextChangedListener(textWatcher);
        descEditText.addTextChangedListener(textWatcher);
        startEditText.addTextChangedListener(textWatcher);
        endEditText.addTextChangedListener(textWatcher);
    }

    private boolean verifyFields() {
        return titleEditText.getText().toString().trim().length() > 0 &&
                descEditText.getText().toString().trim().length() > 0 &&
                startEditText.getText().toString().trim().length() > 0 &&
                endEditText.getText().toString().trim().length() > 0;
    }

    @Override
    public void setComponents() {
        super.setComponents();
        titleEditText = view.findViewById(R.id.project_title);
        descEditText = view.findViewById(R.id.project_description);
        startEditText = view.findViewById(R.id.project_startDate);
        endEditText = view.findViewById(R.id.project_endDate);

        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format),
                new Locale("pt", "BR"));

        startEditText.setInputType(InputType.TYPE_NULL);
        DatePickerDialogHelper.setDatePickerDialog(startEditText, context, sdf);
        endEditText.setInputType(InputType.TYPE_NULL);
        DatePickerDialogHelper.setDatePickerDialog(endEditText, context, sdf);

    }

    @Override
    public void savePart() {
        if (verifyFields()) {
            bundle.clear();
            bundle.putString("TITLE", titleEditText.getText().toString());
            bundle.putString("DESC", descEditText.getText().toString());
            bundle.putString("START", startEditText.getText().toString());
            bundle.putString("END", endEditText.getText().toString());
            listener.onPartFilled(1, bundle);
        }
    }

}