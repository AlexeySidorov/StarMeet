package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;

import java.util.ArrayList;
import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Adapters.CustomViewPagerItemAdapter;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.R;

import static starmeet.convergentmobile.com.starmeet.Helpers.DpPxHelper.*;


/**
 * Created by alexeysidorov on 26.03.2018.
 */

public class ProfileFragment extends BaseFragment {

    private BottomNavigationView bottomNavigationView;
    private EventsProfileFragment events;
    private FragmentManager fragmentManager;
    private OrdersProfileFragment orders;
    private ProfileInfoFragment info;
    private PaymentInfoProfileFragment payment;
    private WishListProfileFragment wishlist;
    private FrameLayout frameLayout;
    private MenuItem prevMenuItem;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_profile_user, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initPage();
        initClick();
        settingLoadingPositionScreen();
        setHasOptionsMenu(true);
        setSettings();

        pageIndex = R.id.action_events;
        loadingFragment(events, true);
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        frameLayout = getActivity().findViewById(R.id.profile_container);
    }

    @SuppressWarnings("ConstantConditions")
    private void initPage() {
        events = new EventsProfileFragment();
        orders = new OrdersProfileFragment();
        info = new ProfileInfoFragment();
        payment = new PaymentInfoProfileFragment();
        wishlist = new WishListProfileFragment();
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
                    case R.id.action_events: {
                        pageIndex = R.id.action_events;
                        loadingFragment(events, false);
                        return true;
                    }
                    case R.id.action_orders: {
                        pageIndex = R.id.action_orders;
                        loadingFragment(orders, false);
                        return true;
                    }
                    case R.id.action_info: {
                        pageIndex = R.id.action_info;
                        loadingFragment(info, false);
                        return true;
                    }
                    case R.id.action_payment: {
                        pageIndex = R.id.action_payment;
                        loadingFragment(payment, false);
                        return true;
                    }
                    case R.id.action_wishlist: {
                        pageIndex = R.id.action_wishlist;
                        loadingFragment(wishlist, false);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setSettings() {

       /* ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(events);
        fragments.add(orders);
        fragments.add(info);
        fragments.add(payment);
        fragments.add(wishlist);

        CustomViewPagerItemAdapter adapter = new CustomViewPagerItemAdapter(fragmentManager, fragments);

        /viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    private void loadingFragment(Fragment fragment, boolean isAdd) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (isAdd)
            transaction.add(R.id.profile_container, fragment).commit();
        else
            transaction.replace(R.id.profile_container, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        lp.bottomMargin = (int) dpToPx(Objects.requireNonNull(getContext()), 55);
        frameLayout.invalidate();
    }

    public void settingLoadingPositionScreen() {
        Intent i = Objects.requireNonNull(getActivity()).getIntent();
        Integer position = i.getIntExtra("Screen", -1);
        if (position == -1 || position == pageIndex) return;

        switch (position) {
            case 0: {
                bottomNavigationView.setSelectedItemId(R.id.action_events);
                break;
            }
            case 1: {
                bottomNavigationView.setSelectedItemId(R.id.action_orders);
                break;
            }
            case 2: {
                bottomNavigationView.setSelectedItemId(R.id.action_info);
                break;
            }
            case 3: {
                bottomNavigationView.setSelectedItemId(R.id.action_payment);
                break;
            }
            case 4: {
                bottomNavigationView.setSelectedItemId(R.id.action_wishlist);
                break;
            }
        }
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
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}