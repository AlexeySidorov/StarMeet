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
import starmeet.convergentmobile.com.starmeet.Adapters.OrdersAdapter;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.PagingRecyclerViewScrollListener;
import starmeet.convergentmobile.com.starmeet.Models.Order;
import starmeet.convergentmobile.com.starmeet.Models.OrderGroup;
import starmeet.convergentmobile.com.starmeet.Models.OrderModel;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.OrderListResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class OrdersProfileFragment extends Fragment implements AdapterClickListener<OrderModel> {
    private ExpandableListView orders;
    private PagingRecyclerViewScrollListener listner;
    private OrdersAdapter orderAdapter;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.orders_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initOrderList();

        if (orderAdapter == null || orderAdapter.getGroupCount() == 0)
            getOrders(0, 10);
    }

    private void initViews() {
        orders = Objects.requireNonNull(getActivity()).findViewById(R.id.recycler_view_orders);
    }

    private void getOrders(final int offset, int count) {
        Call<OrderListResponse> orders = StarMeetApp.getApi().getOrders(offset, count);
        orders.enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderListResponse> call, @NonNull Response<OrderListResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    OrderListResponse result = response.body();
                    ArrayList<OrderGroup> orders = new ArrayList<>();
                    assert result != null;

                    for (int index = 0; index < result.items.size(); index++) {
                        Order order = result.items.get(index);
                        OrderModel model = new OrderModel();
                        model.createUtcDate = order.createUtcDate;
                        model.event = order.event;
                        model.id = order.id;
                        model.itemsCount = order.itemsCount;
                        model.offerType = order.offerType;
                        model.price = order.price;
                        model.status = order.status;

                        ArrayList<OrderModel> models = new ArrayList<>();
                        models.add(model);

                        OrderGroup group = new OrderGroup("", models);
                        group.setName(model.event.celebrity.firstName + " " + model.event.celebrity.lastName);
                        group.setDate(model.event.startUtcTime);
                        group.setCurrency(model.price.currency == null ? "INR" : model.price.currency);
                        group.setPrice(model.price.value);
                        orders.add(group);
                    }

                    initOrderList(orders);

                } else
                    LogUtil.logE("Orders error", response.code() + ". Orders is empty");
            }

            @Override
            public void onFailure(@NonNull Call<OrderListResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void initOrderList(ArrayList<OrderGroup> orders) {
        if (orderAdapter == null) {
            orderAdapter = new OrdersAdapter(getActivity(), orders);
            orderAdapter.setAdapterListener(this);
            this.orders.setAdapter(orderAdapter);
        } else
            orderAdapter.addNewItems(orders);
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

        if (orderAdapter != null && orders.getAdapter() == null)
            orders.setAdapter(orderAdapter);
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
