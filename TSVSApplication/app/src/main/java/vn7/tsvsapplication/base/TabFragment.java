package vn7.tsvsapplication.base;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import vn7.tsvsapplication.*;
import vn7.tsvsapplication.adapter.MyViewPagerAdapter;
import vn7.tsvsapplication.back_end.TSVSparser;


public class TabFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //TabLayout tab
    private String[] titles = {};
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public static TabFragment newInstance(String[] titles) {
        TabFragment newFragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("titles", titles);
        newFragment.setArguments(bundle);
        return newFragment;
    }


    private void init(View v) {
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) v.findViewById(R.id.view_pager);

        //set mode
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //loop add tab
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        setViewPager();
    }

    public void setViewPager() {
        if (titles != null) {
            if (Arrays.equals(titles, getResources().getStringArray(R.array.records_array))) {
                fragments.add(new AbsenceRecordsFragment());
                fragments.add(new RewardRecordsFragment());
            } else if (Arrays.equals(titles, getResources().getStringArray(R.array.results_array))) {
                fragments.add(new PastYearsResultsFragment());
                fragments.add(new MidtermResultsFragment());
            } else if (Arrays.equals(titles, getResources().getStringArray(R.array.cowbei_array))) {
                fragments.add(WebFragment.newInstance(TSVSparser.FB_kao_bei_TSVS));
                fragments.add(WebFragment.newInstance(TSVSparser.kao_bei_TSVS));
                // fragments.add(new CowBeiSubmitFragment());
                viewPager.setPadding(0, 0, 0, 0);
            }
            MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getActivity().getFragmentManager(), titles, fragments);
            viewPager.setAdapter(viewPagerAdapter);
            viewPagerAdapter.notifyDataSetChanged();
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            titles = args.getStringArray("titles");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab, container, false);
        init(v);

        // Inflate the layout for this fragment
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
