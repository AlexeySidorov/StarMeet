package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Ticket;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Request.CheckoutRequest;
import starmeet.convergentmobile.com.starmeet.Response.PayTmloadResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class EventPaymentPayTmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private HolderClickListener<Ticket> listener;
    private AppCompatTextView currencyPrice;
    private AppCompatButton buyButton;
    private AppCompatRadioButton payTmCheck;
    private Ticket model;
    private LinearLayout purchaseBlock;
    private final View view;
    private AppCompatActivity currentActivity;

    @SuppressLint("SetTextI18n")
    public EventPaymentPayTmHolder(View itemView) {
        super(itemView);
        payTmCheck = itemView.findViewById(R.id.pay_check);
        currencyPrice = itemView.findViewById(R.id.currency_price);
        buyButton = itemView.findViewById(R.id.buy_pay_tm_button);
        purchaseBlock = itemView.findViewById(R.id.purchase_price_block);
        view = itemView.findViewById(R.id.title_line);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void Bind(Context context, Ticket model) {
        currentActivity = ((AppCompatActivity)context);
        this.model = model;
        payTmCheck.setChecked(model.isCheck);
        currencyPrice.setText(model.isUsd ?
                CurrentHelper.getCurrencyFormat(model.amountUsd, true)
                : CurrentHelper.getCurrencyFormat(model.amountInr, false));
        buyButton.setOnClickListener(this);
        payTmCheck.setOnClickListener(this);

        if (!model.isCheck) {
            purchaseBlock.setVisibility(View.GONE);
            buyButton.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
    }

    public void setClickListener(HolderClickListener<Ticket> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_pay_tm_button: {
                final CheckoutRequest req = new CheckoutRequest();
                req.currency = model.isUsd ? "USD" : "INR";
                req.eventId = model.eventId;
                req.quantity = model.ticketCount;
                req.offerTypeId = model.payOfferId;
                Call<PayTmloadResponse> request = StarMeetApp.getApi().createPaytm(req);
                request.enqueue(new Callback<PayTmloadResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PayTmloadResponse> call,
                                           @NonNull Response<PayTmloadResponse> response) {
                        if (response.code() == 200 || response.body() != null) {
                            payment(response.body());

                        } else {
                            LogUtil.logE("Payment error", " " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PayTmloadResponse> call, @NonNull Throwable t) {
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
                break;
            }
        }
    }

    private void payment(PayTmloadResponse paytm) {
        PaytmPGService Service = PaytmPGService.getStagingService();
        HashMap<String, String> paramMap = new HashMap<>();

        paramMap.put("CALLBACK_URL", paytm.payload.url);
        paramMap.put("CHANNEL_ID", paytm.payload.channel);
        paramMap.put("CHECKSUMHASH", paytm.payload.checkSum);
        paramMap.put("CUST_ID", paytm.payload.custId.toString());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.payload.industryType);
        paramMap.put("MID", paytm.payload.mid);
        paramMap.put("ORDER_ID", paytm.payload.orderId);
        paramMap.put("TXN_AMOUNT", paytm.payload.txnAmount);
        paramMap.put("WEBSITE", paytm.payload.webSite);

        PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);

        Service.startPaymentTransaction(currentActivity, true,
                true, new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Log.d("LOG", "Payment Transaction: " + inErrorMessage);
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        //Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        Log.d("LOG", "network is not available");
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Toast.makeText(currentActivity,
                                "Authentication failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.d("LOG", "Error loading " + iniErrorCode + " " + inErrorMessage);
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {

                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                    }

                });
    }

}
