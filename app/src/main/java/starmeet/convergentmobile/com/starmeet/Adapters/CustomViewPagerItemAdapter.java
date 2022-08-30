package starmeet.convergentmobile.com.starmeet.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class CustomViewPagerItemAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mFragmentTitles;

    public CustomViewPagerItemAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new ArrayList<>();
        this.mFragmentTitles = new ArrayList<>();
    }

    public CustomViewPagerItemAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = (ArrayList<Fragment>) fragments;
        this.mFragmentTitles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        int index = mFragments.indexOf(object);
        if (index < 0) {
            index = POSITION_NONE;
        }
        return index;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    public void clearFragments() {
        mFragments.clear();
       // mFragmentTitles.clear();
    }

    public void setFragments(List<Fragment> fragments) {
        mFragments = (ArrayList<Fragment>) fragments;
        notifyDataSetChanged();
    }

}
