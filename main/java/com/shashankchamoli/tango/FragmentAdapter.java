package com.shashankchamoli.tango;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager manager){
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new chat();
            case 1:
                return new contacts();
            default:
                return new status();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "chat";
            case 1:
                return  "contacts";
            default:
                return "status";
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
