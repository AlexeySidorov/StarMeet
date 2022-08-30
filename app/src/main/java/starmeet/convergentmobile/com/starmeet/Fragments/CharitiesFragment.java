package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.CharityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.CharitiesAdapter;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Charity;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

public class CharitiesFragment extends BaseFragment implements AdapterClickListener<Charity>,
        ApproachingEventHelper.ApproachingListener {
    private RecyclerView charities;
    private CharitiesAdapter adapter;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_charities, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initCharitiesList();
        setCharityItems();
        setHasOptionsMenu(true);
        ApproachingEventHelper.getInstance().ApproachingEventListener(this);
    }

    private void initViews() {
        charities = Objects.requireNonNull(getActivity()).findViewById(R.id.charity_list);
    }

    @Override
    public void ItemClick(Charity item) {
        if (item == null) return;

        Gson mapper = new Gson();
        String json = mapper.toJson(item);

        Intent i = new Intent(getActivity(), CharityActivity.class);
        i.putExtra("Charity", json);
        startActivity(i);
    }

    @Override
    public void ElementItemClick(Charity item, int elementId) {

    }

    @Override
    public void visibleTimer(boolean isVisible) {

    }

    private void initCharitiesList() {
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        charities.setLayoutManager(layoutManager);
        charities.setHasFixedSize(true);
        adapter = new CharitiesAdapter(getActivity(), new ArrayList<Charity>());
        adapter.setOnClickListener(this);
        charities.setAdapter(adapter);
    }

    private void setCharityItems() {
        Call<ArrayList<Charity>> charitiesResult = StarMeetApp.getApi().getCharities();
        charitiesResult.enqueue(new Callback<ArrayList<Charity>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Charity>> call, @NonNull Response<ArrayList<Charity>> response) {
                if (response.code() == 200 && response.body() != null) {
                    ArrayList<Charity> charities = response.body();
                    adapter.addNewItems(charities);
                } else
                    LogUtil.logE("Charities error", response.code() + ". Charities is empty");
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Charity>> call, @NonNull Throwable t) {
                LogUtil.logE("Charities error", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initNotification();
    }

    @Override
    public void ApproachingEvent(boolean isVisibleNotification) {
        initNotification();
    }

    @Override
    public void getTimeStartChat(String time) {

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
