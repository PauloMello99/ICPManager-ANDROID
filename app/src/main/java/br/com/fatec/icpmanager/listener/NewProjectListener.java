package br.com.fatec.icpmanager.listener;

import android.os.Bundle;

public interface NewProjectListener {
    void onProjectInfoFilled();
    void onPartFilled(int part, Bundle bundle);
}