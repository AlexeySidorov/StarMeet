package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.OrderModel;
import starmeet.convergentmobile.com.starmeet.R;

public class EventCelebrityChildHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AppCompatTextView type;
    private final AppCompatTextView charity;
    private final AppCompatTextView category;
    private final AppCompatTextView createDate;
    private final AppCompatButton view;
    private HolderClickListener<OrderModel> listener;

    public EventCelebrityChildHolder(View itemView) {
        super(itemView);

        type = itemView.findViewById(R.id.offer_type);
        charity = itemView.findViewById(R.id.charity_event);
        category = itemView.findViewById(R.id.category_event);
        createDate = itemView.findViewById(R.id.create_event);
        view = itemView.findViewById(R.id.view_event);
    }

    @SuppressLint("SetTextI18n")
    public void Bind(OrderModel model) {
        charity.setText(model.event.charity.title);
        category.setText(model.event.category.title);
        type.setText(model.offerType.name);
        createDate.setText(DateHelper.UtcToDate(model.event.createUtcTime, "dd MMMM yyyy, hh:mm a"));
        view.setOnClickListener(this);
        view.setTag(model);
    }

    public void setHolderListener(HolderClickListener<OrderModel> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.clickElement((OrderModel) view.getTag(), 0);
    }
}
