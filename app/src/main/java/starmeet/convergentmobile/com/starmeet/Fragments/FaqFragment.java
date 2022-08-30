package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.FaqAdapter;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Faq;
import starmeet.convergentmobile.com.starmeet.Models.FaqGroup;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.FaqResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

public class FaqFragment extends BaseFragment implements ApproachingEventHelper.ApproachingListener {
    private FaqAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_faq, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        setHasOptionsMenu(true);
    }

    private void initViews() {
        recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.recycler_view_faq);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        ApproachingEventHelper.getInstance().ApproachingEventListener(this);

        loadFaq();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (adapter != null)
            adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (adapter != null)
            adapter.onRestoreInstanceState(savedInstanceState);
    }

    private void loadFaq() {
        Call<ArrayList<FaqResponse>> faqs = StarMeetApp.getApi().getFaqs();
        faqs.enqueue(new Callback<ArrayList<FaqResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<FaqResponse>> call,
                                   @NonNull Response<ArrayList<FaqResponse>> response) {
                if (response.code() == 200 && response.body() != null) {
                    ArrayList<FaqResponse> faqList = response.body();
                    ArrayList<FaqGroup> list = new ArrayList<>();

                    assert faqList != null;
                    for (int index = 0; index < faqList.size(); index++) {
                        Faq faq = new Faq();
                        faq.setFaq(faqList.get(index).answer);
                        ArrayList<Faq> faqs = new ArrayList<>();
                        faqs.add(faq);
                        list.add(new FaqGroup(faqList.get(index).question, faqs));
                    }

                    adapter = new FaqAdapter(list);
                    recyclerView.setAdapter(adapter);

                } else
                    LogUtil.logE("Faq error", response.code() + ". Faq is empty");
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<FaqResponse>> call, @NonNull Throwable t) {
                LogUtil.logE("Faq error", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ApproachingEventHelper.getInstance().init();
    }

    @Override
    public void ApproachingEvent(boolean isVisibleNotification) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.setIconNotification(isVisibleNotification, new CallbackEmptyFunc() {
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

    @Override
    public void getTimeStartChat(String time) {

    }

    @Override
    public void visibleTimer(boolean isVisible) {
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
