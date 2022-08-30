package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.SuccessActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.PaymentCardAdapter;
import starmeet.convergentmobile.com.starmeet.Fragments.CustomProgressDialog;
import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.UpdateHelper;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.UpdateData;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.Models.Ticket;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.CheckoutContextRequest;
import starmeet.convergentmobile.com.starmeet.Request.CheckoutRequest;
import starmeet.convergentmobile.com.starmeet.Response.PayResponse;
import starmeet.convergentmobile.com.starmeet.Response.PaymentCardResponse;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;


/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class EventPaymentCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        AdapterClickListener<Card>, UpdateData.UpdateListener {
    private final View view;
    private HolderClickListener<Ticket> listener;
    private AppCompatTextView currencyPrice;
    private AppCompatButton buyButton;
    private AppCompatRadioButton payTmCheck;
    private Context context;
    private Ticket model;
    private LinearLayout purchaseBlock;
    private LinearLayout newCardBlock;
    private AppCompatTextView addNewCard;
    private RecyclerView cardList;
    private PaymentCardAdapter adapter;
    private Card item;
    private AppCompatActivity currentActivity;

    @SuppressLint("SetTextI18n")
    public EventPaymentCardHolder(View itemView) {
        super(itemView);
        payTmCheck = itemView.findViewById(R.id.pay_check);
        currencyPrice = itemView.findViewById(R.id.currency_price);
        buyButton = itemView.findViewById(R.id.buy_usd_button);
        purchaseBlock = itemView.findViewById(R.id.purchase_price_block);
        addNewCard = itemView.findViewById(R.id.add_new_card);
        newCardBlock = itemView.findViewById(R.id.add_new_card_block);
        cardList = itemView.findViewById(R.id.card_list);
        view = itemView.findViewById(R.id.title_line);
    }

    @SuppressLint({"CheckResult", "SetTextI18n", "ResourceAsColor"})
    public void Bind(Context context, Ticket model) {
        this.context = context;
        currentActivity = ((AppCompatActivity)context);
        this.model = model;
        payTmCheck.setChecked(model.isCheck);
        currencyPrice.setText(model.isUsd ?
                CurrentHelper.getCurrencyFormat(model.amountUsd, true)
                : CurrentHelper.getCurrencyFormat(model.amountInr, false));
        buyButton.setOnClickListener(this);
        payTmCheck.setOnClickListener(this);
        purchaseBlock.setVisibility(model.isCheck ? View.VISIBLE : View.GONE);
        buyButton.setVisibility(model.isCheck ? View.VISIBLE : View.GONE);
        newCardBlock.setVisibility(model.isCheck ? View.VISIBLE : View.GONE);
        cardList.setVisibility(model.isCheck ? View.VISIBLE : View.GONE);
        view.setVisibility(model.isCheck ? View.VISIBLE : View.GONE);
        buyButton.setEnabled(false);
        addNewCard.setOnClickListener(this);
        UpdateHelper.getInstance().updateListener(this);
        initCardsAdapter();

       // if (model.isCheck && adapter.getItemCount() == 0)
       //    loadingCards();
    }

    public void setClickListener(HolderClickListener<Ticket> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_usd_button: {
                CheckoutContextRequest checkout = new CheckoutContextRequest();
                checkout.checkoutContext.currency = model.isUsd ? "USD" : "INR";
                checkout.checkoutContext.eventId = model.eventId;
                checkout.checkoutContext.quantity = model.ticketCount;
                checkout.checkoutContext.offerTypeId = model.payOfferId;
                checkout.existingPaymentCardId = item.id;

                final CustomProgressDialog progressDialog = DialogService.ProgressDialog(currentActivity, "Please wait");

                final Call<PayResponse> request = StarMeetApp.getApi().completePaymentCard(checkout);
                request.enqueue(new Callback<PayResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PayResponse> call, @NonNull Response<PayResponse> response) {
                        if (response.code() == 200 && response.body() != null) {
                            PayResponse payResponse = response.body();
                            Gson gson = new GsonBuilder().create();
                            String json = gson.toJson(payResponse);

                            Handler mainHandler = new Handler(context.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            };
                            mainHandler.post(myRunnable);

                            initNavErrorOrSuccessScreen("Success", json);

                        } else if (response.code() == 400) {

                            Handler mainHandler = new Handler(context.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            };
                            mainHandler.post(myRunnable);

                            ResponseBody body = response.errorBody();
                            if (body != null) {
                                try {
                                    String str = body.string();
                                    String[] result = str.split("ModelState");
                                    starmeet.convergentmobile.com.starmeet.Response.Response res =
                                            new starmeet.convergentmobile.com.starmeet.Response.Response();

                                    if (result.length == 0) return;

                                    res.modelState = result[result.length == 2 ? 1 : 0];

                                    if (res.modelState != null)
                                        initNavErrorOrSuccessScreen("Error", res.modelState);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    LogUtil.logE("Payment error", " " + e.getMessage());
                                }

                            } else {
                                LogUtil.logE("Payment error", " " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PayResponse> call, @NonNull Throwable t) {

                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        };
                        mainHandler.post(myRunnable);

                        LogUtil.logE("Payment error", t.getMessage(), t);
                    }
                });

                break;
            }
            case R.id.pay_check: {
                model.isCheck = true;
                listener.clickElement(model, R.id.pay_check);
                purchaseBlock.setVisibility(View.VISIBLE);
                buyButton.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                loadingCards();
                break;
            }
            case R.id.add_new_card: {
                listener.clickElement(model, R.id.add_new_card);
                break;
            }
        }
    }

    private void initNavErrorOrSuccessScreen(String typeMessage, String content) {
        Intent i = new Intent(currentActivity,
                SuccessActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(typeMessage, content);
        currentActivity.startActivity(i);
    }

    private void initCardsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity);
        cardList.setLayoutManager(layoutManager);
        adapter = new PaymentCardAdapter(currentActivity, new ArrayList<Card>());
        adapter.setOnClickListener(this);
        cardList.setAdapter(adapter);
    }

    private void loadingCards() {

        final CustomProgressDialog progressDialog = DialogService.ProgressDialog(currentActivity, "Please wait");

        CheckoutRequest req = new CheckoutRequest();
        req.currency = model.isUsd ? "USD" : "INR";
        req.eventId = model.eventId;
        req.quantity = model.ticketCount;
        req.offerTypeId = model.payOfferId;

        Call<PaymentCardResponse> request = StarMeetApp.getApi().checkoutInit(req);
        request.enqueue(new Callback<PaymentCardResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentCardResponse> call, @NonNull Response<PaymentCardResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    PaymentCardResponse payResponse = response.body();

                    if (adapter.getItemCount() > 0)
                        adapter.clear();

                    ArrayList<Card> cardArrayList = new ArrayList<>();
                    assert payResponse != null;
                    if (payResponse.paymentCardMethod.existingCards.size() > 0)
                        cardArrayList.addAll(payResponse.paymentCardMethod.existingCards);

                    adapter.addNewItems(cardArrayList);

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    };
                    mainHandler.post(myRunnable);

                } else {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            DialogService.MessageDialog(currentActivity,
                                    "Error", "Error. Credit cards list is not available", "OK", null);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentCardResponse> call, @NonNull Throwable t) {
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        DialogService.MessageDialog(currentActivity,
                                "Error", "Error. Credit cards list is not available", "OK", null);
                    }
                };
                mainHandler.post(myRunnable);
            }
        });
    }

    @Override
    public void ItemClick(Card item) {

    }

    @Override
    public void ElementItemClick(Card item, int elementId) {
        this.item = item;
        switch (elementId) {
            case R.id.card_check: {
                buyButton.setEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    buyButton.setTextColor(context.getColor(R.color.white));
                } else {
                    buyButton.setTextColor(context.getResources().getColor(R.color.white));
                }
                break;
            }
        }
    }

    @Override
    public void Update() {
        loadingCards();
    }
}
