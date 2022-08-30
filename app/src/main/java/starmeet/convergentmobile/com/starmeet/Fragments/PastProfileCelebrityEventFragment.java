package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.DetailsActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.CelebrityMyEventsAdapter;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.PagingRecyclerViewScrollListener;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.Models.EventCelebrityGroup;
import starmeet.convergentmobile.com.starmeet.Models.OrderModel;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.EventSearchRequest;
import starmeet.convergentmobile.com.starmeet.Response.EventResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

public class PastProfileCelebrityEventFragment extends Fragment implements AdapterClickListener<OrderModel> {

    private ExpandableListView orders;
    private PagingRecyclerViewScrollListener listner;
    private CelebrityMyEventsAdapter eventAdapter;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.past_orders_celebrity_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initOrderList();

        if (eventAdapter == null || eventAdapter.getGroupCount() == 0)
            getOrders(0, 10);
    }

    private void initViews() {
        orders = Objects.requireNonNull(getActivity()).findViewById(R.id.past_recycler_view_orders);
    }

    private void getOrders(final int offset, int count) {
        EventSearchRequest search = new EventSearchRequest();
        search.offset = offset;
        search.limit = count;
        search.mode = "Past";

        Call<EventResponse> orders = StarMeetApp.getApi().SearchCreateEvent(search);
        orders.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventResponse> call,
                                   @NonNull Response<EventResponse> response) {
                if (response.code() == 200 && response.body() != null) {

                    EventResponse result = response.body();
                    ArrayList<EventCelebrityGroup> events = new ArrayList<>();
                    assert result != null;

                    for (int index = 0; index < result.items.size(); index++) {
                        Event event = result.items.get(index);
                        OrderModel model = new OrderModel();
                        model.event = event;
                        model.offerType = event.offerType;
                        model.id = event.id;

                        ArrayList<OrderModel> models = new ArrayList<>();
                        models.add(model);

                        EventCelebrityGroup group = new EventCelebrityGroup("", models);
                        group.setTitle(event.type.title + " " + DateHelper.UtcToDate(event.startUtcTime,
                                "EEE, d MMM yyyy hh:mm a"), event.status.title);
                        events.add(group);
                    }

                    initOrderList(events);

                } else
                    LogUtil.logE("Orders error", response.code() + ". Orders is empty");
            }

            @Override
            public void onFailure(@NonNull Call<EventResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void initOrderList(ArrayList<EventCelebrityGroup> orders) {
        if (eventAdapter == null) {
            eventAdapter = new CelebrityMyEventsAdapter(getActivity(), orders);
            eventAdapter.setAdapterListener(this);
            this.orders.setAdapter(eventAdapter);
        } else
            eventAdapter.addNewItems(orders);
    }

    private int visibleThreshold = 10;
    private int previousTotal = 0;
    private boolean loading = true;
    private int page;

    private void initOrderList() {
        orders.setGroupIndicator(null);
        orders.setChildIndicator(null);
        orders.setDividerHeight(0);
        orders.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        page++;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    if (page == 0) page = 1;

                    getOrders((page * 10), 10);
                    loading = true;
                }
            }
        });

        if (eventAdapter != null && eventAdapter.getGroupCount() > 0)
            orders.setAdapter(eventAdapter);
    }

    @Override
    public void ItemClick(OrderModel item) {
        Intent i = new Intent(getActivity(), DetailsActivity.class);
        i.putExtra("event_id", item.id);
        i.putExtra("unvisible_block", true);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }

    @Override
    public void ElementItemClick(OrderModel item, int elementId) {

    }
}