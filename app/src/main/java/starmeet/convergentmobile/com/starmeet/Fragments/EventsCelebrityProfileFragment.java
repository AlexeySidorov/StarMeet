package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class EventsCelebrityProfileFragment extends Fragment {
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.events_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initPager();
    }

    private void initViews() {
        viewPager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);
        viewPagerTab = getActivity().findViewById(R.id.view_pager_tab);
    }

    private void initPager() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                Objects.requireNonNull(getChildFragmentManager()),
                FragmentPagerItems.with(getContext())
                .add(R.string.profile_upcoming, UpcomingCelebrityProfileEventFragment.class)
                .add(R.string.profile_past, PastProfileCelebrityEventFragment.class)
                .create());

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        final LinearLayout lyTabs = (LinearLayout) viewPagerTab.getChildAt(0);
        changeTabsTitleTypeFace(lyTabs, 0);

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changeTabsTitleTypeFace(lyTabs, i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void changeTabsTitleTypeFace(LinearLayout ly, int position) {
        for (int j = 0; j < ly.getChildCount(); j++) {
            TextView tvTabTitle = (TextView) ly.getChildAt(j);
            tvTabTitle.setTypeface(null, Typeface.NORMAL);
            if (j == position) tvTabTitle.setTypeface(null, Typeface.BOLD);
        }
    }
}
