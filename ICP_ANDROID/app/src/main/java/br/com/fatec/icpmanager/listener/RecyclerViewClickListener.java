package br.com.fatec.icpmanager.listener;

import android.view.View;

public interface RecyclerViewClickListener {
    void recyclerViewListClicked(View v, int position, int TAG, String TYPE);
}