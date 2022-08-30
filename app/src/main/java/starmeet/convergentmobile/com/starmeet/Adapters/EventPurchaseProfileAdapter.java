package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.EventsPurchaseHolder;
import starmeet.convergentmobile.com.starmeet.Models.PurchasedModel;
import starmeet.convergentmobile.com.starmeet.R;

public class EventPurchaseProfileAdapter extends RecyclerSwipeAdapter<EventsPurchaseHolder> {

    private Context cxt;
    private ArrayList<PurchasedModel> array;
    private View view;

    public EventPurchaseProfileAdapter(Context cxt, ArrayList<PurchasedModel> array) {
        this.cxt = cxt;
        this.array = array;
    }

    @NonNull
    @Override
    public EventsPurchaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_purchased, parent, false);
        return new EventsPurchaseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsPurchaseHolder viewHolder, int position) {
        PurchasedModel item = array.get(position);

        if (item != null) {
            viewHolder.Bind(cxt, item);
            viewHolder.resizeView();
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public void addNewItems(ArrayList<PurchasedModel> events) {
        array.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_block;
    }
}
