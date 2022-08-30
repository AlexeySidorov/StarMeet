package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Order;
import starmeet.convergentmobile.com.starmeet.Models.OrderModel;
import starmeet.convergentmobile.com.starmeet.R;

public class OrderChildHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AppCompatTextView qty;
    private final AppCompatTextView status;
    private final AppCompatTextView type;
    private final AppCompatTextView purchase;
    private final AppCompatButton view;
    private HolderClickListener<OrderModel> listener;

    public OrderChildHolder(View itemView) {
        super(itemView);

        qty = itemView.findViewById(R.id.qty);
        status = itemView.findViewById(R.id.status_event);
        type = itemView.findViewById(R.id.type_event);
        purchase = itemView.findViewById(R.id.purchase_event);
        view = itemView.findViewById(R.id.view_event);
    }

    @SuppressLint("SetTextI18n")
    public void Bind(OrderModel model) {
        qty.setText(model.itemsCount.toString());
        status.setText(model.status.title);
        type.setText(model.offerType.title);
        purchase.setText(DateHelper.UtcToDate(model.event.startUtcTime, "dd MMMM yyyy"));
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
