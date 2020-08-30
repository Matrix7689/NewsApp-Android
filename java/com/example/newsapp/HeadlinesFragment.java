package com.example.newsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;

public class HeadlinesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_headlines, container, false);
        viewPager =  v.findViewById(R.id.viewpager_content);
        System.out.println(viewPager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        HeadlinesAdapter adapter = new HeadlinesAdapter(getChildFragmentManager());
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        adapter.addFragment(new WorldFragment(), "World");
        adapter.addFragment(new BusinessFragment(), "Business");
        adapter.addFragment(new PoliticsFragment(),"Politics");
        adapter.addFragment(new SportsFragment(),"Sports");
        adapter.addFragment(new TechnologyFragment(),"Technology");
        adapter.addFragment(new ScienceFragment(),"Science");
        viewPager.setAdapter(adapter);

        return v;
    }
}