package com.example.obdenergy.obdenergy.Activities;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.example.obdenergy.obdenergy.R;

//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBar.TabListener;

/**
 * Created by sumayyah on 5/31/14.
 */
public class TabListener implements ActionBar.TabListener {

    Fragment fragment;

    public TabListener(Fragment fragment){
        this.fragment = fragment;
    }

    //TODO: fix v7.tablistener vs tablistener issues - basically actionbar vs v7.actionbar
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(R.id.fragment_container, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
