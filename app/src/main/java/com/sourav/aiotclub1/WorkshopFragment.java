package com.sourav.aiotclub1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class WorkshopFragment extends Fragment {

    public WorkshopFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workshop, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);


        VPAdapter vpAdapter = new VPAdapter(getChildFragmentManager());

        vpAdapter.addFragments(new NewsFragment(), "News");
        vpAdapter.addFragments(new ForumFragment(), "Forum");
        vpAdapter.addFragments(new TopFragment(), "Top");
        vpAdapter.addFragments(new FavoritesFragment(), "Favorite");


        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
