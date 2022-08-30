package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.PaymentCardHolder;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.R;

public class PaymentCardAdapter extends RecyclerView.Adapter implements HolderClickListener<Card> {

    private Context cxt;
    private ArrayList<Card> array;
    private AdapterClickListener<Card> listener;

    public PaymentCardAdapter(Context cxt, ArrayList<Card> array) {
        this.cxt = cxt;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_cards, parent, false);
        return new PaymentCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Card item = array.get(position);

        if (item != null) {
            ((PaymentCardHolder) holder).Bind(cxt, item);
            ((PaymentCardHolder) holder).setClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public void addNewItems(ArrayList<Card> cards) {
        array.addAll(cards);
        notifyDataSetChanged();
    }

    public void setOnClickListener(AdapterClickListener<Card> listener) {
        this.listener = listener;
    }

    @Override
    public void clickElement(Card item, int elementId) {
        switch (elementId) {
            case R.id.card_check: {
                item.isCheck = true;

                for (int index = 0; index < array.size(); index++) {
                    Card card = array.get(index);

                    if (!card.id.equals(item.id) && card.isCheck) {
                        card.isCheck = false;
                        notifyDataSetChanged();
                        break;
                    }
                }

                listener.ElementItemClick(item, R.id.card_check);
                break;
            }
        }
    }

    public void clear() {
        array.clear();
        notifyDataSetChanged();
    }
}
