package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AddNewCardActivity;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.EventPurchaseAdapter;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackNotification;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.Models.NavigationModel;
import starmeet.convergentmobile.com.starmeet.Models.PaymentType;
import starmeet.convergentmobile.com.starmeet.Models.Ticket;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;


import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.Buy;
import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.Date;

/**
 * Created by alexeysidorov on 23.03.2018.
 */

public class EventDetailsFragment extends Fragment implements AdapterClickListener<Ticket>,
        View.OnClickListener, CallbackNotification {

    private Event event;
    private AppCompatImageView avatar;
    private AppCompatTextView name;
    private AppCompatTextView description;
    private RecyclerView priceList;
    private AppCompatTextView date;
    private AppCompatImageView addDate;
    private Ticket item;
    private EventPurchaseAdapter adapter;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.event_details_fragment, null);
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initLogic();
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        name = getActivity().findViewById(R.id.celebrity_name);
        description = getActivity().findViewById(R.id.celebrity_description);
        avatar = getActivity().findViewById(R.id.avatar_celebrity);
        priceList = getActivity().findViewById(R.id.purchase_list);
        date = getActivity().findViewById(R.id.date_event_value);
        addDate = getActivity().findViewById(R.id.add_or_remove);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("CheckResult")
    private void initLogic() {
        Bundle bundle = getArguments();
        event = new Event();

        Boolean unvisible = bundle.getBoolean("unvisible_block", false);
        priceList.setVisibility(unvisible ? View.GONE : View.VISIBLE);

        if (bundle != null) {
            Gson mapper = new Gson();
            String json = bundle.getString("event_model");
            event = mapper.fromJson(json, Event.class);
        }

        if (event == null || event.celebrity == null) return;

        name.setText(event.celebrity.firstName != null && !event.celebrity.firstName.isEmpty()
                || event.celebrity.lastName != null && !event.celebrity.lastName.isEmpty()
                ? event.celebrity.firstName + " " + event.celebrity.lastName
                : "Not specified");
        description.setText(event.longDescription != null && !event.longDescription.isEmpty()
                ? event.longDescription : "Not specified");

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        avatar.getLayoutParams().width = width - 75;
        avatar.getLayoutParams().height = width - 75;
        avatar.requestLayout();

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.skipMemoryCache(true);
        Glide.with(getActivity()).load(event.celebrity.photoUrl).apply(options).into(avatar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false);

        adapter = new EventPurchaseAdapter(getActivity(), getDefaultTickets());
        adapter.setOnClickListener(this);
        priceList.setLayoutManager(layoutManager);
        priceList.setHasFixedSize(true);
        priceList.setAdapter(adapter);

        if (MyContext.getInstance().userRole == 2)
            priceList.setVisibility(View.GONE);

        date.setText(DateHelper.UtcToDate(event.startUtcTime, "EEEE, d MMMM yyyy hh:mm a"));
        setIconAddOrRemoveWishList(event.isInWishList);
        addDate.setOnClickListener(this);
    }

    @Override
    public void ItemClick(Ticket item) {

    }

    private boolean isUsd = false;

    @Override
    public void ElementItemClick(Ticket item, int elementId) {
        this.item = item;

        switch (elementId) {
            case R.id.buy_button: {
                if (MyContext.getInstance().isAuthUser) {
                    setPaymentInfo();
                } else {
                    HashMap<ActivityNextType, Class> activities = new HashMap<>();
                    activities.put(ActivityNextType.None, AppCompatActivity.class);
                    NavigationHelper.getInstance().setNextActivity(activities, Buy);
                    NavigationHelper.getInstance().setNotificationListener(this);

                    Intent i = new Intent(getActivity(), AuthActivity.class);
                    startActivity(i);
                }

                break;
            }
            case R.id.close: {
                adapter.clear();
                adapter.addNewElements(getDefaultTickets());

                break;
            }
            case R.id.add_new_card: {
                HashMap<ActivityNextType, Class> activities = new HashMap<>();
                activities.put(ActivityNextType.Activity1, AppCompatActivity.class);
                NavigationHelper.getInstance().setNextActivity(activities, NavigationModel.None);
                NavigationHelper.getInstance().setNotificationListener(this);

                Intent i = new Intent(getActivity(), AddNewCardActivity.class);
                i.putExtra("isProfile", false);
                Objects.requireNonNull(getActivity()).startActivity(i);

                break;
            }
            case R.id.currency_value: {
                isUsd = item.isUsd;
                break;
            }
            case R.id.purchase_info: {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogService.CustomMessageDialog(
                                Objects.requireNonNull((AppCompatActivity) getActivity()), item.isLotteryPurchase ? "For each entry. " +
                                        "You can purchase multiple lottery entries which will increase your chance" +
                                        " of winning" : "This purchase will cancel all lottery entries " +
                                        "and you will automatically become a winner.", "OK", null);
                    }
                });

                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_or_remove: {
                if (MyContext.getInstance().isAuthUser) {
                    addWishList();
                } else {
                    HashMap<ActivityNextType, Class> activities = new HashMap<>();
                    activities.put(ActivityNextType.None, AppCompatActivity.class);
                    NavigationHelper.getInstance().setNextActivity(activities, Date);
                    NavigationHelper.getInstance().setNotificationListener(this);

                    Intent i = new Intent(getActivity(), AuthActivity.class);
                    startActivity(i);
                }

                break;
            }
        }
    }

    private ArrayList<Ticket> getDefaultTickets() {
        ArrayList<Ticket> tickets = new ArrayList<>();

        if (event.Raffle != null && event.Raffle.ticketPrices != null) {
            Ticket ticketRaff = new Ticket(event.id, event.Raffle.ticketPrices.amountInr,
                    event.Raffle.ticketPrices.amountUsd, false, PaymentType.None,
                    false, event.offerType, event.Raffle, true);
            ticketRaff.setPayOfferTicket(event.Raffle.offerTypeId);
            tickets.add(ticketRaff);
        }

        if (event.drectPurchase != null && event.drectPurchase.prices != null) {
            Ticket ticket = new Ticket(event.id, event.drectPurchase.prices.amountInr,
                    event.drectPurchase.prices.amountUsd, false, PaymentType.None,
                    false, event.offerType, event.Raffle, false);
            ticket.setPayOfferTicket(event.drectPurchase.offerTypeId);
            tickets.add(ticket);
        }

        return tickets;
    }

    @Override
    public void onNotification(NavigationModel model) {
        switch (model) {
            case Buy:
                setPaymentInfo();
                break;
            case Date: {
                addWishList();
                break;
            }
        }
    }

    private void addWishList() {
        if (event == null || event.id == 0) return;

        Call<Void> request =
                StarMeetApp.getApi().getUpdateWishListStateEvent(event.id, !event.isInWishList);

        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == 200) {
                    event.isInWishList = !event.isInWishList;
                    setIconAddOrRemoveWishList(event.isInWishList);
                } else
                    LogUtil.logE("Add wishList error", " " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                LogUtil.logE("Add wishList error", t.getMessage(), t);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void setIconAddOrRemoveWishList(boolean state) {

        if (MyContext.getInstance().userRole <= 1) {

            Drawable drawable = VectorDrawableCompat.create(getResources(),
                    state ? R.drawable.remove_with_list
                            : R.drawable.add_with_list, null);
            addDate.setImageDrawable(drawable);
        } else
            addDate.setVisibility(View.GONE);
    }

    private void setPaymentInfo() {
        if (item == null) return;

        adapter.clear();

        ArrayList<Ticket> tickets = new ArrayList<>();
        Ticket ticket = new Ticket(item.eventId, item.amountInr, item.amountUsd, item.isUsd,
                PaymentType.Info, false, item.offerType, item.raffle, item.isLotteryPurchase);
        ticket.setTicketCount(item.ticketCount);
        ticket.setPayOfferTicket(item.payOfferId);
        tickets.add(ticket);

        if (!isUsd) {
            ticket = new Ticket(item.eventId, item.amountInr, item.amountUsd, false,
                    PaymentType.PayTm, false, item.offerType, item.raffle, item.isLotteryPurchase);
            ticket.setTicketCount(item.ticketCount);
            ticket.setPayOfferTicket(item.payOfferId);
            tickets.add(ticket);
        }

        ticket = new Ticket(item.eventId, item.amountInr, item.amountUsd, item.isUsd,
                PaymentType.Card, isUsd, item.offerType, item.raffle, item.isLotteryPurchase);
        ticket.setTicketCount(item.ticketCount);
        ticket.setPayOfferTicket(item.payOfferId);
        tickets.add(ticket);

        adapter.addNewElements(tickets);
    }
}
