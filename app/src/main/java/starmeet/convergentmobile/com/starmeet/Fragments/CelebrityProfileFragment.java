package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Helpers.DpPxHelper;
import starmeet.convergentmobile.com.starmeet.R;


/**
 * Created by alexeysidorov on 26.03.2018.
 */

public class CelebrityProfileFragment extends BaseFragment {

    private BottomNavigationView bottomNavigationView;
    private EventsCelebrityProfileFragment events;
    private FragmentManager fragmentManager;
    private ProfileInfoFragment info;
    private CelebrityBioProfileFragment bio;
    private FrameLayout frameLayout;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_profile_celebrity, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initPage();
        initClick();
        setHasOptionsMenu(true);

        pageIndex = R.id.action_bio;
        loadingFragment(bio, true);
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        frameLayout = getActivity().findViewById(R.id.profile_container);
    }

    @SuppressWarnings("ConstantConditions")
    private void initPage() {
        events = new EventsCelebrityProfileFragment();
        info = new ProfileInfoFragment();
        bio = new CelebrityBioProfileFragment();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    private int pageIndex;

    private void initClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == pageIndex)
                    return false;

                switch (item.getItemId()) {
                    case R.id.action_bio: {
                        pageIndex = R.id.action_bio;
                        loadingFragment(bio, false);
                        return true;
                    }
                    case R.id.action_events: {
                        pageIndex = R.id.action_events;
                        loadingFragment(events, false);
                        return true;
                    }
                    case R.id.action_info: {
                        loadingFragment(info, false);
                        pageIndex = R.id.action_info;
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void loadingFragment(Fragment fragment, boolean isAdd) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (isAdd)
            transaction.add(R.id.profile_container, fragment).commit();
        else
            transaction.replace(R.id.profile_container, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        lp.bottomMargin = (int) DpPxHelper.dpToPx(Objects.requireNonNull(getContext()), 55);
        frameLayout.invalidate();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem myAccountMenuItem = menu.findItem(R.id.my_account);
        myAccountMenuItem.setVisible(false);
        final MenuItem myActionMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEvents(query);
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_account: {
                navigationProfile();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}