package br.com.fatec.icpmanager.fragment.project;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.com.fatec.icpmanager.listener.NewProjectListener;

public abstract class CustomFragment extends Fragment {

    protected NewProjectListener listener;
    protected Bundle bundle;
    protected View view;
    protected Context context;
    protected boolean filled;

     public CustomFragment(NewProjectListener listener, Context context) {
        this.listener = listener;
        this.context = context;
        filled = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setComponents(){
        bundle = new Bundle();
    }

    public abstract void savePart();

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}