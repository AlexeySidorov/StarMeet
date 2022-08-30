package starmeet.convergentmobile.com.starmeet.Adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import starmeet.convergentmobile.com.starmeet.Controls.CustomViewPager;


public class ProfileViewPagerItemAdapter extends FragmentPagerItemAdapter {

    private int mCurrentPosition = -1;

    public ProfileViewPagerItemAdapter(FragmentManager fm, FragmentPagerItems pages) {
        super(fm, pages);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            CustomViewPager pager = (CustomViewPager) container;

            if (fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }
}
