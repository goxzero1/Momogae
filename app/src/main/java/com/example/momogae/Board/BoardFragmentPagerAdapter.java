package com.example.momogae.Board;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BoardFragmentPagerAdapter extends FragmentPagerAdapter {
    private String _query = null;
    private int currentPosition = 0;
    private ArrayList<Fragment> mFragments = new ArrayList();
    private ArrayList<String> mTitles = new ArrayList();

    public BoardFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (currentPosition == 0) {
            ((PostListFragment) object).updateUi(_query);
        } else if (currentPosition == 1) {
            ((PostListFragment) object).updateUi(_query);
        } else if (currentPosition == 2)  {
            ((PostListFragment) object).updateUi(_query);
        }

        return super.getItemPosition(object);
    }

    public void search(String query) {
        _query = query;
        notifyDataSetChanged();
    }

    public void setFragment(List<Fragment> fragments, List<String> titles) {
        mFragments.clear();
        mTitles.clear();
        mFragments.addAll(fragments);
        mTitles.addAll(titles);
        notifyDataSetChanged();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }
}
