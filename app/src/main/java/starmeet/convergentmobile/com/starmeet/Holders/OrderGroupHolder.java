package starmeet.convergentmobile.com.starmeet.Holders;


import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import java.text.NumberFormat;
import java.util.Date;

import starmeet.convergentmobile.com.starmeet.Helpers.CurrentHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.R;

public class OrderGroupHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView date;
    private AppCompatTextView titleFio;
    private AppCompatImageView imgButton;
    private AppCompatTextView price;
    private AppCompatTextView currency;
    private Context cxt;

    public OrderGroupHolder(View itemView) {
        super(itemView);

        titleFio = itemView.findViewById(R.id.fio);
        date = itemView.findViewById(R.id.date_order);
        price = itemView.findViewById(R.id.price);
        currency = itemView.findViewById(R.id.currency);
        imgButton = itemView.findViewById(R.id.img_button);
        imgButton.setImageResource(R.drawable.arrow_green_small);
    }

    public void setContext(Context cxt) {
        this.cxt = cxt;
    }

    public void setFio(String title) {
        titleFio.setText(title);
    }

    public void setDate(Date date) {
        this.date.setText(DateHelper.UtcToDate(date, "EEE, d MMM yyyy hh:mm a"));
    }

    public void setPrice(double price) {
        this.price.setText(CurrentHelper.getCurrencyFormat(price, false));
    }

    public void setCurrency(String currencyValue) {
        currency.setText(currencyValue);
    }

    public void resizeView() {
        DisplayMetrics metrics = cxt.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int widthDefault = titleFio.getLayoutParams().width + imgButton.getLayoutParams().width +
                currency.getLayoutParams().width + price.getLayoutParams().width;
        date.getLayoutParams().width = width - widthDefault - 85;
        date.invalidate();
    }

    public void expand() {
        imgButton.setImageResource(R.drawable.arrow_up);
    }

    public void collapse() {
        imgButton.setImageResource(R.drawable.arrow_green_small);
    }
}
