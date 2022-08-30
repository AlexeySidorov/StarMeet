package starmeet.convergentmobile.com.starmeet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.DetailsActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.EventsAdapter;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.PagingRecyclerViewScrollListener;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.EventRequest;
import starmeet.convergentmobile.com.starmeet.Response.EventResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.Buy;

/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class WishListProfileFragment extends Fragment implements AdapterClickListener<Event> {
    private RecyclerView eventList;
    private EventsAdapter eventAdapter;
    private PagingRecyclerViewScrollListener listener;
    private Event item;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.wishlist_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initEventList();

        if (eventAdapter == null || eventAdapter.getItemCount() == 0)
            loadEvents(0, 6);
    }

    private void initViews() {
        eventList = Objects.requireNonNull(getActivity()).findViewById(R.id.wishlist_list);
    }

    private void loadEvents(int offset, int limit) {

        final EventRequest request = new EventRequest();
        request.offset = offset;
        request.limit = limit;
        request.filters = new ArrayList<>();
        request.sortMethod = "ClosingNext";
        request.inMyWishListOnly = true;

        Call<EventResponse> response = StarMeetApp.getApi().getEventSearch(request);
        response.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventResponse> call, @NonNull Response<EventResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    EventResponse events = response.body();

                    if (events == null || events.items == null || events.items.size() == 0) {
                        listener.loading = true;
                        return;
                    }

                    listener.setTotalCount(events.total);
                    initEventList(events.items);

                } else
                    LogUtil.logE("Events error", response.code() + ". Events is empty");
            }

            @Override
            public void onFailure(@NonNull Call<EventResponse> call, @NonNull Throwable t) {
                LogUtil.logE("Events error", t.getMessage(), t);
            }
        });
    }

    private void initEventList() {
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
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

        if (eventAdapter != null && eventAdapter.getItemCount() > 0)
            eventList.setAdapter(eventAdapter);
    }

    private void initEventList(ArrayList<Event> events) {

        ArrayList<Event> evnts = new ArrayList<Event>();
        for (Event ev : events) {
            if (ev.isInWishList)
                evnts.add(ev);
        }

        if (eventAdapter == null) {
            eventAdapter = new EventsAdapter(getActivity(), evnts);
            eventAdapter.setOnClickListener(this);
            eventList.setAdapter(eventAdapter);
        } else
            eventAdapter.addNewItems(evnts);

        listener.stopLoading();
    }

    @Override
    public void ItemClick(Event item) {
        if (item == null) return;

        this.item = item;
        detailsEventNav();
    }

    @Override
    public void ElementItemClick(final Event item, int elementId) {
        switch (elementId) {
            case R.id.add_celebrity: {
                Call<Void> request =
                        StarMeetApp.getApi().getUpdateWishListStateEvent(item.id, !item.isInWishList);

                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {
                            eventAdapter.removeElement(item);
                        } else
                            LogUtil.logE("Add wishList error", " " + response.code());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        LogUtil.logE("Add wishList error", t.getMessage(), t);
                    }
                });
                break;
            }

            case R.id.buy_button: {
                detailsEventNav();
                break;
            }
        }
    }

    private void detailsEventNav() {
        if (item == null || getActivity() == null) return;

        Intent i = new Intent(getActivity(), DetailsActivity.class);
        i.putExtra("event_id", item.id);

        Objects.requireNonNull(getActivity()).startActivity(i);
    }
}
