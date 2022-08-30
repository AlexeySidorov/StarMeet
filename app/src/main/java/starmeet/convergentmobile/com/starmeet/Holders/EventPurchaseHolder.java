package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Ticket;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class EventPurchaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AppCompatTextView currency1;
    private final AppCompatTextView currency2;
    private final AppCompatButton buyButton;
    private final AppCompatImageView purchaseInfo;
    private final View ticketCount;
    private HolderClickListener<Ticket> listener;
    private AppCompatTextView currencyValue;
    private AppCompatTextView purchaseTitle;
    private AppCompatImageView arrow;
    private AppCompatActivity currentActivity;
    private MaterialEditText tickets;

    @SuppressLint("SetTextI18n")
    public EventPurchaseHolder(View itemView) {
        super(itemView);
        currency1 = itemView.findViewById(R.id.currency_value1);
        currency2 = itemView.findViewById(R.id.currency_value2);
        purchaseInfo = itemView.findViewById(R.id.purchase_info);
        purchaseTitle = itemView.findViewById(R.id.purchase_title);
        buyButton = itemView.findViewById(R.id.buy_button);
        arrow = itemView.findViewById(R.id.arrow_down);
        ticketCount = itemView.findViewById(R.id.ticketCount);
        currencyValue = itemView.findViewById(R.id.currency_value);
        tickets = itemView.findViewById(R.id.tickets);
        currencyValue.setText("INR");
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void Bind(Context context, Ticket model) {
        currentActivity = ((AppCompatActivity) context);
        currency1.setText(CurrentHelper.getCurrencyFormat(model.amountInr, false));
        currency2.setText(CurrentHelper.getCurrencyFormat(model.amountUsd, true));
        purchaseTitle.setText(model.isLotteryPurchase ? model.offerType.title : "Purchase Now");
        ticketCount.setVisibility(model.isLotteryPurchase ? View.VISIBLE : View.GONE);
        tickets.setText(model.isLotteryPurchase ? "1" : "");
        buyButton.setTag(model);
        purchaseInfo.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        arrow.setOnClickListener(this);
        currencyValue.setOnClickListener(this);
    }

    public void setClickListener(HolderClickListener<Ticket> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_button: {
                Ticket item = (Ticket) buyButton.getTag();

                if (item.isLotteryPurchase) {
                    String countStr = tickets.getText().toString();
                    if (!countStr.isEmpty()) {
                        Integer ticketCount = Integer.parseInt(countStr, 10);
                        if (ticketCount == 0) {
                            return;
                        }

                        item.ticketCount = ticketCount;
                    }
                }

                listener.clickElement(item, R.id.buy_button);
                break;
            }
            case R.id.purchase_info: {
                listener.clickElement((Ticket) buyButton.getTag(), R.id.purchase_info);
                break;
            }
            case R.id.arrow_down:
            case R.id.currency_value: {
                setDialogCurrency();
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDialogCurrency() {
        ArrayList<String> curr = new ArrayList<>();
        curr.add("INR");
        curr.add("USD");

        DialogService.bottomSheetNumericDialog(currentActivity,
                curr.toArray(new String[curr.size()]), "INR", new DialogCallback<String>() {
                    @Override
                    public void onResult(String result) {
                        currencyValue.setText(result);
                        Ticket ticket = (Ticket) buyButton.getTag();
                        ticket.isUsd = result.equals("USD");
                        buyButton.setTag(ticket);
                        listener.clickElement(ticket, R.id.currency_value);
                    }

                    @Override
                    public void onClose() {

                    }
                });
    }
}
