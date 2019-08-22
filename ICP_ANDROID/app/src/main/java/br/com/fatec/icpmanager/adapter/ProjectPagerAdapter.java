package br.com.fatec.icpmanager.adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.fatec.icpmanager.fragment.project.CustomFragment;

public class ProjectPagerAdapter extends FragmentPagerAdapter {
    private List<CustomFragment> fragmentList = new ArrayList<>();

    public ProjectPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CustomFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(CustomFragment fragment) {
        fragmentList.add(fragment);
    }

}
