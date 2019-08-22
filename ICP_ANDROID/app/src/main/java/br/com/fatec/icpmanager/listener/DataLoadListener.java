package br.com.fatec.icpmanager.listener;

import java.util.List;

public interface DataLoadListener<T> {
    void onDataLoaded(List<T> list);
    void onDataCancelled(String error);
}