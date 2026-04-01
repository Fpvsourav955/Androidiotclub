package com.sourav.aiotclub1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventPagerAdapter extends FragmentStateAdapter {

    public EventPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EventDetailsFragment();
            case 1:
                return new AttendanceFragment();
            case 2:
                return new FeedbackFragment();
            default:
                return new EventDetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
