package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.PayResponse;

public class SuccessFragment extends Fragment implements View.OnClickListener {

    private AppCompatTextView dateOrder;
    private AppCompatTextView typeOrder;
    private AppCompatTextView amount;
    private AppCompatTextView event;
    private AppCompatTextView startTime;
    private AppCompatTextView errorMessage;
    private AppCompatTextView titleSuccess;
    private View successBlock;
    private AppCompatButton myEvent;
    private AppCompatButton myOrders;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_success, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initClick();
        initScreen();
    }

    private void initViews() {
        successBlock = Objects.requireNonNull(getActivity()).findViewById(R.id.success_block);
        dateOrder = successBlock.findViewById(R.id.date_order);
        typeOrder = successBlock.findViewById(R.id.type_order);
        amount = successBlock.findViewById(R.id.amount_order);
        event = successBlock.findViewById(R.id.event_id);
        startTime = successBlock.findViewById(R.id.star_time_event);
        myEvent = Objects.requireNonNull(getActivity()).findViewById(R.id.my_event);
        myOrders = Objects.requireNonNull(getActivity()).findViewById(R.id.my_order);
        errorMessage = Objects.requireNonNull(getActivity()).findViewById(R.id.error_message);
        titleSuccess = Objects.requireNonNull(getActivity()).findViewById(R.id.title_success);
    }

    private void initClick() {
        myOrders.setOnClickListener(this);
        myEvent.setOnClickListener(this);
    }

    private void initScreen() {
        Intent i = Objects.requireNonNull(getActivity()).getIntent();
        if (i != null) {
            String error = i.getStringExtra("Error");
            if (error != null) {
                initErrorBlock(error);
            } else {
                String successResult = i.getStringExtra("Success");
                if (successResult != null) {
                    Gson gson = new GsonBuilder().create();
                    PayResponse model = gson.fromJson(successResult, PayResponse.class);
                    initSuccessBlock(model);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initErrorBlock(String error) {
        titleSuccess.setText("ERROR");
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText(error);
    }

    @SuppressLint("SetTextI18n")
    private void initSuccessBlock(PayResponse model) {
        if (model == null || model.order == null) return;

        titleSuccess.setText("SUCCESS");
        successBlock.setVisibility(View.VISIBLE);

        event.setText(model.order.id.toString());
        typeOrder.setText(model.order.offerType.title);
        String price = model.order.price.currency.equals("INR")
                ? CurrentHelper.getCurrencyFormat(model.order.price.value, false)
                : CurrentHelper.getCurrencyFormat(model.order.price.value, true);
        amount.setText(model.order.price.currency + " " + price);
        startTime.setText(DateHelper.UtcToDate(model.order.event.startUtcTime, "EEEE, d MMMM yyyy hh:mm a"));
        dateOrder.setText(DateHelper.UtcToDate(model.order.createUtcDate, "EEEE, d MMMM yyyy hh:mm a"));
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), ProfileUserActivity.class);

        switch (v.getId()) {
            case R.id.my_event:
                i.putExtra("Screen", R.id.action_events);
                break;
            case R.id.my_order:
                i.putExtra("Screen", R.id.action_orders);
                break;
        }

        Objects.requireNonNull(getActivity()).startActivity(i);
        getActivity().finish();
    }
}
