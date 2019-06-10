package com.example.astroweather2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new BasicData();
            /*case 1:
                return null; //new MoonFragment();*/
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 1;
    }
}