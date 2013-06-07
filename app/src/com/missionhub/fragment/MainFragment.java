//package com.missionhub.fragment;
//
//import android.support.v4.widget.DrawerLayout;
//import android.view.Gravity;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.view.Menu;
//import com.missionhub.activity.MainActivity;
//
//public abstract class MainFragment extends BaseFragment {
//
//    public MainFragment() {}
//
//    public abstract void onPrepareActionBar(ActionBar actionBar);
//
//    public void onPrepareDrawerOptionsMenu(Menu menu) {}
//
//    public void onPreparePrimaryOptionsMenu(Menu menu) {}
//
//    final public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//
//        if (getMainActivity().getDrawerLayout().isDrawerOpen(Gravity.LEFT)) {
//            onPrepareDrawerOptionsMenu(menu);
//        } else {
//            onPreparePrimaryOptionsMenu(menu);
//        }
//    }
//
//    public MainActivity getMainActivity() {
//        return (MainActivity) getSupportActivity();
//    }
//
//    public DrawerLayout getDrawerLayout() {
//        return getMainActivity().getDrawerLayout();
//    }
//}