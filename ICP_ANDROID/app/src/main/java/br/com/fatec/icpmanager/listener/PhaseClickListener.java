package br.com.fatec.icpmanager.listener;

import br.com.fatec.icpmanager.model.Phase;

public interface PhaseClickListener {
    void onPhaseItemClicked(Phase phase, int position, String TAG);
}