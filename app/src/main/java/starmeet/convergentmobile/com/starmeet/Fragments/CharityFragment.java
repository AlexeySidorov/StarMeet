package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Charity;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

public class CharityFragment extends BaseFragment implements View.OnClickListener,
        ApproachingEventHelper.ApproachingListener {

    private WebView web;
    private AppCompatButton webButton;
    private AppCompatImageView img;
    private Charity charity;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_charity, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initDataView();
        initNotification();
        setHasOptionsMenu(true);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        img = getActivity().findViewById(R.id.img);
        webButton = getActivity().findViewById(R.id.web_button);
        web = getActivity().findViewById(R.id.description);
        web.getSettings().setJavaScriptEnabled(true);
        ApproachingEventHelper.getInstance().ApproachingEventListener(this);
    }

    @SuppressLint("CheckResult")
    private void initDataView() {
        Intent i = Objects.requireNonNull(getActivity()).getIntent();
        String json = i.getStringExtra("Charity");

        if (json != null && !json.isEmpty()) {
            Gson mapper = new Gson();
            charity = mapper.fromJson(json, Charity.class);

            if (charity != null) {
                RequestOptions options = new RequestOptions();
                options.fitCenter();
                options.skipMemoryCache(true);
                Glide.with(getActivity()).load(charity.LogoUrl).apply(options).into(img);

                webButton.setText(charity.webSiteTitle);
                webButton.setOnClickListener(this);
                web.loadDataWithBaseURL(null, charity.Description, "text/html", "utf-8", null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(charity.url));
        startActivity(browserIntent);
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
