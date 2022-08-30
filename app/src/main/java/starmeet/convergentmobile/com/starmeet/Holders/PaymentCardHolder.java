package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class PaymentCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AppCompatImageView iconCard;
    private final AppCompatRadioButton checkCard;
    private final AppCompatTextView number;
    private HolderClickListener<Card> listener;

    public PaymentCardHolder(View itemView) {
        super(itemView);
        checkCard = itemView.findViewById(R.id.card_check);
        iconCard = itemView.findViewById(R.id.icon_card);
        number = itemView.findViewById(R.id.number_card);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void Bind(Context context, Card model) {
        number.setText("**** **** **** " + model.numberLast4);
        checkCard.setTag(model);
        checkCard.setChecked(model.isCheck);

        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.fitCenter();

        checkCard.setOnClickListener(this);

        switch (model.brand) {
            case "Visa": {
                Glide.with(context).load(R.drawable.group382).apply(options).into(iconCard);
                break;
            }
            case "Discover": {
                Glide.with(context).load(R.drawable.group386).apply(options).into(iconCard);
                break;
            }
            case "MasterCard": {
                Glide.with(context).load(R.drawable.group381).apply(options).into(iconCard);
                break;
            }
            case "American Express": {
                Glide.with(context).load(R.drawable.group387).apply(options).into(iconCard);
                break;
            }
        }
    }

    public void setClickListener(HolderClickListener<Card> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_check: {
                listener.clickElement((Card) checkCard.getTag(), R.id.card_check);
                break;
            }
        }
    }
}
