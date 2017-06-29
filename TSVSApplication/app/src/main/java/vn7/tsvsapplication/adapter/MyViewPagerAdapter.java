package vn7.tsvsapplication.adapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by casper on 2017/4/30.
 */

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> fragments;
    private String[] titles;
    String tag="MyViewPagerAdapter";
    public MyViewPagerAdapter(FragmentManager fm, String[] titles, ArrayList<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {return fragments.size();}

}