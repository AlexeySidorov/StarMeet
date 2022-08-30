package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.CardHolder;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Card;
import starmeet.convergentmobile.com.starmeet.R;

public class CardAdapter extends RecyclerView.Adapter implements HolderClickListener<Card> {

    private Context cxt;
    private ArrayList<Card> array;
    private AdapterClickListener<Card> listener;

    public CardAdapter(Context cxt, ArrayList<Card> array) {
        this.cxt = cxt;
        this.array = array;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Card item = array.get(position);

        if (item != null) {
            ((CardHolder) holder).Bind(cxt, item);
            ((CardHolder) holder).setClickListener(this);
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
            case R.id.remove_card: {
                listener.ElementItemClick(item, R.id.remove_card);
                break;
            }
            case R.id.edit_card: {
                listener.ElementItemClick(item, R.id.edit_card);
                break;
            }
        }
    }

    public void clear() {
        array.clear();
        notifyDataSetChanged();
    }

    public void remove(String id) {
        for (int index = 0; index < array.size(); index++) {
            Card card = array.get(index);

            if (card.id.equals(id)) {
                array.remove(index);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
