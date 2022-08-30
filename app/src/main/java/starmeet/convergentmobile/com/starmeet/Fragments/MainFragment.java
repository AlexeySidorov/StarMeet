package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.DetailsActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.EventsAdapter;
import starmeet.convergentmobile.com.starmeet.Adapters.SliderMainAdapter;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackNotification;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.UpdateHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.PagingRecyclerViewScrollListener;
import starmeet.convergentmobile.com.starmeet.Listners.RecyclerItemClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.UpdateData;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.Models.Filter;
import starmeet.convergentmobile.com.starmeet.Models.MainSlider;
import starmeet.convergentmobile.com.starmeet.Models.NavigationModel;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.EventRequest;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.Response.EventResponse;
import starmeet.convergentmobile.com.starmeet.Response.ListResponse;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.*;

/**
 * Created by alexeysidorov on 26.02.2018.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener, AdapterClickListener<Event>,
        CallbackNotification, UpdateData.UpdateListener, ApproachingEventHelper.ApproachingListener {
    private RecyclerView mainSlider;
    private SliderMainAdapter adapterSlider;
    private LinearLayout events;
    private AppCompatTextView event;
    private AppCompatTextView sortFilter;
    private AppCompatImageView arrowFilter;
    private RecyclerView eventList;
    private EventsAdapter eventAdapter;
    private PagingRecyclerViewScrollListener listner;
    private ActivityCommunicator<String> activityCommunicator;
    private Event item;
    private boolean groupFilter;
    private Parcelable mListState;
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initClickListener();
        initDefaultLoading();
        setHasOptionsMenu(true);
        phoneId(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            activityCommunicator = (ActivityCommunicator<String>) context;
        } catch (Exception exp) {

        }
    }

    @SuppressLint("ApplySharedPref")
    public synchronized static void phoneId(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        mainSlider = getActivity().findViewById(R.id.previews);
        events = getActivity().findViewById(R.id.events);
        event = getActivity().findViewById(R.id.event);
        sortFilter = getActivity().findViewById(R.id.filter_sort);
        arrowFilter = getActivity().findViewById(R.id.arrow_filter);
        eventList = getActivity().findViewById(R.id.main_list);
    }

    private void initClickListener() {
        events.setOnClickListener(this);
        sortFilter.setOnClickListener(this);
        arrowFilter.setOnClickListener(this);
        UpdateHelper.getInstance().updateListener(this);
        ApproachingEventHelper.getInstance().ApproachingEventListener(this);
        ApproachingEventHelper.getInstance().init();
        initNotification();
    }

    private void initMainSlider() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        ArrayList<MainSlider> list = new ArrayList<>();
        list.add(new MainSlider("https://starmeet.eleview.com/images/cat-bg-2.jpg",
                "FILM", "Lorem ipsum dolor sit amet," +
                " consectetur adipiscing elit. Praesent consectetur eget justo et condimentum. " +
                "Fusce eu mi vitae nibh tempus volutpat vel pulvinar leo. Aliquam mauris mauris, accumsan " +
                "vitae augue quis, volutpat molestie risus.", "Film"));
        list.add(new MainSlider("https://starmeet.eleview.com/images/cat-bg-3.jpg",
                "MUSIC", "Lorem ipsum dolor sit amet," +
                " consectetur adipiscing elit. Praesent consectetur eget justo et condimentum. " +
                "Fusce eu mi vitae nibh tempus volutpat vel pulvinar leo. Aliquam mauris mauris, accumsan " +
                "vitae augue quis, volutpat molestie risus.", "Music"));
        list.add(new MainSlider("https://starmeet.eleview.com/images/cat-bg-4.jpg",
                "TV", "", "TV"));

        adapterSlider = new SliderMainAdapter(getActivity(), list);
        mainSlider.setLayoutManager(layoutManager);
        mainSlider.setHasFixedSize(true);
        mainSlider.setAdapter(adapterSlider);
        mainSlider.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mainSlider,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        MainSlider model = adapterSlider.getItem(position);
                        if (model != null) {
                            event.setText(model.eventName);
                            eventValue = model.eventName;

                            if (eventAdapter != null && eventAdapter.getItemCount() > 0)
                                eventAdapter.clear();

                            listner.resetState();
                            loadEvents(0, 10);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
    }

    private void selectEvent() {

        if (MyContext.getInstance().events == null || MyContext.getInstance().events.size() == 0)
            return;

        String defaultValue = event.getText().toString();
        if (defaultValue.isEmpty())
            event.setText(MyContext.getInstance().events.get(0).title);

        ArrayList<String> eventList = new ArrayList<>();

        for (int index = 0; index < MyContext.getInstance().events.size(); index++)
            eventList.add(MyContext.getInstance().events.get(index).title);

        DialogService.bottomSheetNumericDialog(getActivity(), eventList.toArray(new String[eventList.size()]),
                defaultValue, new DialogCallback<String>() {
                    @Override
                    public void onResult(String result) {
                        event.setText(result);
                        eventValue = result;

                        if (eventAdapter != null && eventAdapter.getItemCount() > 0)
                            eventAdapter.clear();

                        listner.resetState();
                        loadEvents(0, 10);
                    }

                    @Override
                    public void onClose() {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.events: {
                selectEvent();
                break;
            }
            case R.id.filter_sort:
            case R.id.arrow_filter: {
                sortBy();
                break;
            }
        }
    }

    private String eventValue;
    private String sortFilterValue;
    private ListResponse eventGroup;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    public void initDefaultLoading() {

        Intent in = getActivity().getIntent();
        int eventId = in.getIntExtra("Event", -1);
        groupFilter = in.getBooleanExtra("GroupFilter", false);
        ListResponse eventType = null;

        if (eventId > 0)
            eventType = getEventType(eventId);

        if (MyContext.getInstance().events == null || MyContext.getInstance().events.size() == 0)
            return;

        if (MyContext.getInstance().eventTypes == null || MyContext.getInstance().eventTypes.size() == 0)
            return;

        String defaultValue = event.getText().toString();
        if (defaultValue.isEmpty() && eventType == null) {
            event.setText(MyContext.getInstance().events.get(0).title);
            eventValue = MyContext.getInstance().events.get(0).title;
        } else {
            event.setText(eventType.title);
            eventValue = eventType.title;
        }

        if (groupFilter && eventGroup == null)
            eventGroup = eventType;
        else
            eventGroup = null;

        String defaultFilterValue = sortFilter.getText().toString();
        if (defaultFilterValue.isEmpty()) {
            sortFilter.setText("Closing Next");
            sortFilterValue = "ClosingNext";
        }

        initMainSlider();
        initEventList();

        loadEvents(0, 10);
    }

    public void sortBy() {
        String defaultValue = sortFilter.getText().toString();
        ArrayList<String> sortFilterList = new ArrayList<>();
        sortFilterList.add("Closing Next");
        sortFilterList.add("Highest Price");
        sortFilterList.add("Lowest Price");
        sortFilterList.add("Closing Last");

        DialogService.bottomSheetNumericDialog(getActivity(), sortFilterList.toArray(new String[sortFilterList.size()]),
                defaultValue, new DialogCallback<String>() {
                    @Override
                    public void onResult(String result) {
                        sortFilter.setText(result);
                        sortFilterValue = result.replaceAll("\\s+", "");

                        if (eventAdapter != null && eventAdapter.getItemCount() > 0)
                            eventAdapter.clear();

                        if (listner != null)
                            listner.resetState();

                        loadEvents(0, 10);
                    }

                    @Override
                    public void onClose() {

                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void loadEvents(int offset, int limit) {
        final EventRequest request = new EventRequest();
        request.offset = offset;
        request.limit = limit;
        request.filters = GetFilter(eventValue);
        request.sortMethod = sortFilterValue;
        request.inMyWishListOnly = false;

        Gson mapper = new Gson();
        String json = mapper.toJson(request);
        LogUtil.logE("Event ", json);

        Call<EventResponse> response = StarMeetApp.getApi().getEventSearch(request);
        response.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventResponse> call, @NonNull Response<EventResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    EventResponse events = response.body();

                    if (events == null || events.items == null || events.items.size() == 0) {
                        listner.loading = true;
                        return;
                    }

                    listner.setTotalCount(events.total);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<Filter> GetFilter(String name) {
        ListResponse event = getEvent(name);
        ArrayList<Filter> filters = new ArrayList<>();

        if (event != null && event.id != 0) {
            Filter filter = new Filter(!isEventType(event) ? "EventCategoryID" : "EventTypeID", event.id);
            filters.add(filter);

            if (groupFilter) {
                Filter groupFilter = new Filter(!isEventType(eventGroup) ? "EventCategoryID" : "EventTypeID", eventGroup.id);

                boolean isAdd = false;
                for (int index = 0; index < filters.size(); index++) {
                    if (filters.get(index).value == groupFilter.value &&
                            filters.get(index).key == groupFilter.key) {
                        isAdd = true;
                        break;
                    }
                }

                if (!isAdd)
                    filters.add(groupFilter);
            }

            return filters;
        }

        return filters;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isEventType(ListResponse event) {
        if (MyContext.getInstance().eventTypes == null)
            return false;

        for (int index = 0; index < MyContext.getInstance().eventTypes.size(); index++) {
            if (Objects.equals(event.title,
                    MyContext.getInstance().eventTypes.get(index).title))
                return true;
        }

        return false;
    }

    @SuppressLint("ObsoleteSdkInt")
    private ListResponse getEvent(final String name) {
        if (MyContext.getInstance().events == null) return null;

        for (int index = 0; index < MyContext.getInstance().events.size(); index++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(MyContext.getInstance().events.get(index).title, name))
                    return MyContext.getInstance().events.get(index);
            }
        }

        return null;
    }

    @SuppressLint("ObsoleteSdkInt")
    private ListResponse getEventType(int id) {
        if (MyContext.getInstance().events == null) return null;

        for (int index = 0; index < MyContext.getInstance().eventTypes.size(); index++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(id,
                        MyContext.getInstance().eventTypes.get(index).id))
                    return MyContext.getInstance().eventTypes.get(index);
            }
        }

        return null;
    }

    private void initEventList(ArrayList<Event> events) {
        if (eventAdapter == null) {

            if (events == null)
                events = new ArrayList<>();

            eventAdapter = new EventsAdapter(getActivity(), events);
            eventAdapter.setOnClickListener(this);
            eventList.setAdapter(eventAdapter);
        } else
            eventAdapter.addNewItems(events);

        listner.stopLoading();
    }

    private void initEventList() {
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        eventList.setLayoutManager(layoutManager);
        eventList.setHasFixedSize(true);
        listner = new PagingRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page == 0) page = 1;

                loadEvents((page * 10), 10);
            }
        };
        eventList.addOnScrollListener(listner);
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
                searchEvents(query, eventAdapter);
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void ItemClick(Event item) {
        if (item == null) return;

        this.item = item;
        detailsEventNav();
    }

    @Override
    public void ElementItemClick(Event item, int elementId) {
        if (item == null) return;

        this.item = item;

        switch (elementId) {
            case R.id.buy_button: {
                if (MyContext.getInstance().isAuthUser)
                    detailsEventNav();
                else {
                    HashMap<ActivityNextType, Class> activities = new HashMap<>();
                    activities.put(ActivityNextType.None, AppCompatActivity.class);
                    NavigationHelper.getInstance().setNextActivity(activities, Buy);
                    NavigationHelper.getInstance().setNotificationListener(this);

                    Intent i = new Intent(getActivity(), AuthActivity.class);
                    startActivity(i);
                }
                break;
            }
            case R.id.add_celebrity: {
                if (MyContext.getInstance().isAuthUser) {
                    addWishList();
                } else {
                    HashMap<ActivityNextType, Class> activities = new HashMap<>();
                    activities.put(ActivityNextType.None, AppCompatActivity.class);
                    NavigationHelper.getInstance().setNextActivity(activities, Add);
                    NavigationHelper.getInstance().setNotificationListener(this);

                    Intent i = new Intent(getActivity(), AuthActivity.class);
                    startActivity(i);
                }
                break;
            }
        }
    }

    @Override
    public void onNotification(NavigationModel model) {
        switch (model) {
            case Add:
                addWishList();
                break;
            case Buy: {
                detailsEventNav();
                break;
            }
        }

        activityCommunicator.passDataToActivity(null, 0);
    }

    private void detailsEventNav() {
        if (item == null) return;

        Intent i = new Intent(getActivity(), DetailsActivity.class);
        i.putExtra("event_id", item.id);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }

    private void addWishList() {
        if (item == null) return;

        Call<Void> request =
                StarMeetApp.getApi().getUpdateWishListStateEvent(item.id, !item.isInWishList);

        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == 200) {
                    item.isInWishList = !item.isInWishList;
                    eventAdapter.notifyDataSetChanged();
                } else
                    LogUtil.logE("Add wishList error", " " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                LogUtil.logE("Add wishList error", t.getMessage(), t);
            }
        });
    }

    @Override
    public void Update() {
        if (eventAdapter != null)
            eventAdapter.clear();

        loadEvents(0, 10);
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

        if (activity != null)
            activity.setIconNotification(event != null && event.id > 0, new CallbackEmptyFunc() {
                @Override
                public void Click() {
                    navigationProfile();
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        initNotification();

        Intent intent = Objects.requireNonNull(getActivity()).getIntent();
        String searchLine = intent.getStringExtra("SearchLine");

        if (searchLine != null && !searchLine.isEmpty() && eventAdapter != null)
            eventAdapter.searchEvent(searchLine);
    }
}
