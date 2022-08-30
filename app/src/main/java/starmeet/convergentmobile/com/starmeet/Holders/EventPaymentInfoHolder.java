package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Ticket;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class EventPaymentInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AppCompatTextView currencyValue;
    private AppCompatTextView currencyTitle;
    private AppCompatImageView close;
    private HolderClickListener<Ticket> listener;
    private Ticket model;

    @SuppressLint("SetTextI18n")
    public EventPaymentInfoHolder(View itemView) {
        super(itemView);
        currencyValue = itemView.findViewById(R.id.currency_value);
        currencyTitle = itemView.findViewById(R.id.currency_title);
        close = itemView.findViewById(R.id.close);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void Bind(Ticket model) {
        this.model = model;
        currencyTitle.setText(model.isUsd ? "USD" : "INR");
        currencyValue.setText(CurrentHelper.getCurrencyFormat(
                model.isUsd ? model.amountUsd : model.amountInr, model.isUsd));
        close.setOnClickListener(this);
    }

    public void setClickListener(HolderClickListener<Ticket> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close: {
                listener.clickElement(model, R.id.close);
                break;
            }
        }
    }
}
