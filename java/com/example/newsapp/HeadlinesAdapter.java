package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HeadlinesAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentSectionList = new ArrayList<>();

    public HeadlinesAdapter(FragmentManager manager) {
        super(manager);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentSectionList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                WorldFragment world = new WorldFragment();
//                return world;
//            case 1:
//                BusinessFragment business = new BusinessFragment();
//                return business;
//            case 2:
//                PoliticsFragment politics = new PoliticsFragment();
//                return politics;
//            case 3:
//                SportsFragment sports = new SportsFragment();
//                return sports;
//            case 4:
//                TechnologyFragment technology = new TechnologyFragment();
//                return technology;
//            case 5:
//                ScienceFragment science = new ScienceFragment();
//                return science;
//            default:
//                return null;
//        }
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentSectionList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentSectionList.get(position);
    }
}
