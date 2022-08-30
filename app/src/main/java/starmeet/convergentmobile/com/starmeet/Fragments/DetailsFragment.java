package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

/**
 * Created by alexeysidorov on 23.03.2018.
 */

public class DetailsFragment extends BaseFragment implements View.OnClickListener,
        ApproachingEventHelper.ApproachingListener {

    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private CustomProgressDialog progressDialog;
    private Event event;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_details, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initPager();
        initNotification();
        setHasOptionsMenu(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        viewPager = getActivity().findViewById(R.id.viewpager);
        viewPagerTab = getActivity().findViewById(R.id.view_pager_tab);
        AppCompatImageView share = getActivity().findViewById(R.id.share);
        share.setOnClickListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    private void initPager() {
        Intent intent = getActivity().getIntent();
        Integer eventId = intent.getIntExtra("event_id", 0);
        GetEventById(eventId);
    }

    @Override
    public void onClick(View v) {
        if (event == null || event.detailsUrl == null || event.detailsUrl.isEmpty() ||
                event.shortDescription == null || event.shortDescription.isEmpty()) return;

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = event.shortDescription + "\n" + event.detailsUrl;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "starmeet.eleview.com");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    private void GetEventById(Integer eventId) {
        if (eventId == 0) return;

        progressDialog = DialogService.ProgressDialog(Objects.requireNonNull(getActivity()), "Please wait");

        Call<Event> event = StarMeetApp.getApi().getEventById(eventId);
        event.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200 && response.body() != null) {
                    initView(response.body());
                    return;
                } else
                    LogUtil.logE("Get event error ", response.toString());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Event> call, @NonNull Throwable t) {
                LogUtil.logE("Get event failure error ", t.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void initView(Event event) {
        this.event = event;

        Gson mapper = new GsonBuilder().create();
        String json = mapper.toJson(event);

        Bundle bundle = new Bundle();
        bundle.putString("event_model", json);

        Intent i = getActivity().getIntent();
        boolean flag = i.getBooleanExtra("unvisible_block", false);
        bundle.putBoolean("unvisible_block", flag);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(),
                FragmentPagerItems.with(getContext())
                        .add(R.string.event_tab_name, EventDetailsFragment.class, bundle)
                        .add(R.string.bio_tab_name, CelebrityBioFragment.class, bundle)
                        .add(R.string.cause, CauseCelebrityFragment.class, bundle)
                        .create());

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });

        ApproachingEventHelper.getInstance().ApproachingEventListener(this);
    }

    @Override
    public void ApproachingEvent(boolean isVisibleNotification) {
        initNotification();
    }

    @Override
    public void getTimeStartChat(String time) {

    }

    @Override
    public void visibleTimer(boolean isVisible) {

    }

    private void initNotification() {
        ApproachingEventResponse event = ApproachingEventHelper.getInstance().getApproachingEvent();

        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.setIconNotification(event != null && event.id > 0, new CallbackEmptyFunc() {
                @Override
                public void Click() {
                    if (MyContext.getInstance().isAuthUser) {
                        Intent i;

                        if (MyContext.getInstance().userRole == 1)
                            i = new Intent(getActivity(), ProfileUserActivity.class);
                        else
                            i = new Intent(getActivity(), ProfileCelebrityActivity.class);

                        startActivity(i);

                    } else {
                        HashMap<ActivityNextType, Class> activities = new HashMap<>();
                        activities.put(ActivityNextType.Activity1, ProfileUserActivity.class);
                        activities.put(ActivityNextType.Activity2, ProfileCelebrityActivity.class);
                        NavigationHelper.getInstance().setNextActivity(activities, None);
                        startActivity(new Intent(getActivity(), AuthActivity.class));
                    }
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

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
