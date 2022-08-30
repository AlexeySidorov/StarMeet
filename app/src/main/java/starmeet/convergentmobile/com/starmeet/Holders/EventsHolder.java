package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class EventsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AppCompatTextView date;
    private final AppCompatTextView name;
    private final AppCompatImageView avatar;
    private final AppCompatTextView description;
    private final AppCompatTextView price;
    private final AppCompatButton buyButton;
    private final AppCompatImageView addOrRemove;
    private HolderClickListener<Event> listener;
    private Context context;

    public EventsHolder(View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date_celebrity);
        name = itemView.findViewById(R.id.name_celebrity);
        avatar = itemView.findViewById(R.id.avatar_celebrity);
        description = itemView.findViewById(R.id.description_celebrity);
        price = itemView.findViewById(R.id.price);
        addOrRemove = itemView.findViewById(R.id.add_celebrity);
        buyButton = itemView.findViewById(R.id.buy_button);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void Bind(Context context, Event model) {
        this.context = context;

        name.setText(model.celebrity.firstName + " " + model.celebrity.lastName);
        description.setText(model.shortDescription);
        String priceText = model.offerType.id == 1
                ? CurrentHelper.getCurrencyFormat(model.drectPurchase.prices.amountInr, false)
                : "Lottery: " + CurrentHelper.getCurrencyFormat(model.drectPurchase.prices.amountInr, false) + "/ ticket";
        price.setText(priceText);

        date.setText(DateHelper.UtcToDate(model.startUtcTime, "EEE, d MMM yyyy hh:mm a"));

        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        Glide.with(context).load(model.celebrity.photoUrl).apply(options).into(avatar);

        setIconSelectOrUnselected(context, model);

        name.setTag(model);
        buyButton.setTag(model);

        name.setOnClickListener(this);
        description.setOnClickListener(this);
        price.setOnClickListener(this);
        date.setOnClickListener(this);
        avatar.setOnClickListener(this);
        addOrRemove.setOnClickListener(this);
        buyButton.setOnClickListener(this);
    }

    public void setClickListener(HolderClickListener<Event> listener) {
        this.listener = listener;
    }

    @SuppressLint("CheckResult")
    private void setIconSelectOrUnselected(Context context, Event model) {
        if (MyContext.getInstance().userRole <= 1) {

            Drawable drawable = VectorDrawableCompat.create(context.getResources(),
                    model.isInWishList ? R.drawable.remove_with_list
                            : R.drawable.add_with_list, null);
            addOrRemove.setImageDrawable(drawable);
        } else
            addOrRemove.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_button: {
                listener.clickElement((Event) buyButton.getTag(), R.id.buy_button);
                break;
            }
            case R.id.add_celebrity: {
                Event model = (Event) buyButton.getTag();
                listener.clickElement(model, R.id.add_celebrity);
                break;
            }
            case R.id.date_celebrity:
            case R.id.name_celebrity:
            case R.id.avatar_celebrity:
            case R.id.description_celebrity:
            case R.id.price: {
                listener.clickElement((Event) name.getTag(), -1);
                break;
            }
        }
    }
}
