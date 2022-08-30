package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Adapters.EventPurchaseProfileAdapter;
import starmeet.convergentmobile.com.starmeet.Listners.PagingRecyclerViewScrollListener;
import starmeet.convergentmobile.com.starmeet.Models.PurchasedModel;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.EventSearchRequest;
import starmeet.convergentmobile.com.starmeet.Response.PurchasedResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

public class PastProfileEventFragment extends Fragment implements Observer{

    private PagingRecyclerViewScrollListener listener;
    private EventPurchaseProfileAdapter eventAdapter;
    private RecyclerView eventList;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.event_past_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initEventList();

        if (eventAdapter == null || eventAdapter.getItemCount() == 0)
            loadEvents(0, 10);
    }

    private void initViews() {
        eventList = Objects.requireNonNull(getActivity()).findViewById(R.id.event_past_list);
        eventList.setNestedScrollingEnabled(false);
    }

    private void loadEvents(int offset, int limit) {
        EventSearchRequest option = new EventSearchRequest();
        option.limit = limit;
        option.offset = offset;
        option.mode = "Past";

        final Call<PurchasedResponse> request = StarMeetApp.getApi().SearchPurchasedEvent(option);
        request.enqueue(new Callback<PurchasedResponse>() {
            @Override
            public void onResponse(@NonNull Call<PurchasedResponse> call, @NonNull Response<PurchasedResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    PurchasedResponse events = response.body();
                    if (events == null || events.items == null || events.items.size() == 0) {
                        listener.loading = true;
                        return;
                    }

                    listener.setTotalCount(events.total);
                    initEventList(events.items);
                } else
                    LogUtil.logE("Purchase error", "List events empty");
            }

            @Override
            public void onFailure(@NonNull Call<PurchasedResponse> call, @NonNull Throwable t) {
                LogUtil.logE("Purchase error", t.getMessage(), t);
            }
        });
    }

    private void initEventList(ArrayList<PurchasedModel> events) {
        if (eventAdapter == null) {
            eventAdapter = new EventPurchaseProfileAdapter(getActivity(), events);
            eventList.setAdapter(eventAdapter);
        } else
            eventAdapter.addNewItems(events);

        listener.stopLoading();
    }

    private void initEventList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        eventList.setLayoutManager(layoutManager);
        eventList.setHasFixedSize(true);
        listener = new PagingRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page == 0) page = 1;

                loadEvents((page * 10), 10);
            }
        };

        eventList.addOnScrollListener(listener);

        if (eventAdapter != null)
            eventList.setAdapter(eventAdapter);
    }

    @Override
    public void update(Observable observable, Object o) {
        loadEvents(0, 10);
    }
}
