//package com.missionhub.ui;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class FragmentArrayPagerAdapter extends FragmentStatePagerAdapter {
//
//    List<Fragment> mFragments = Collections.synchronizedList(new ArrayList<Fragment>());
//
//    public FragmentArrayPagerAdapter(FragmentManager fm) {
//        super(fm);
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return mFragments.get(position);
//    }
//
//    @Override
//    public int getCount() {
//        return mFragments.size();
//    }
//
//    @Override
//    public int getItemPosition(Object item) {
//        return mFragments.indexOf(item);
//    }
//
//    public void addItem(Fragment item) {
//        mFragments.add(item);
//        notifyDataSetChanged();
//    }
//
//    public void removeItem(Fragment item) {
//        mFragments.remove(item);
//        notifyDataSetChanged();
//    }
//
//    public void removeItem(int id) {
//        mFragments.remove(id);
//        notifyDataSetChanged();
//    }
//}