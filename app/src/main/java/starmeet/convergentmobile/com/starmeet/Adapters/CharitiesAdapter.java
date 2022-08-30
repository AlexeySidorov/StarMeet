package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.CharityHolder;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Charity;
import starmeet.convergentmobile.com.starmeet.R;

public class CharitiesAdapter extends RecyclerView.Adapter implements HolderClickListener<Charity> {
    private Context cxt;
    private ArrayList<Charity> array;
    private AdapterClickListener<Charity> listener;

    public CharitiesAdapter(Context cxt, ArrayList<Charity> array) {
        this.cxt = cxt;
        this.array = array;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_charity, parent, false);
        return new CharityHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Charity item = array.get(position);

        if (item != null) {
            ((CharityHolder) holder).Bind(cxt, item);
            ((CharityHolder) holder).setClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public void addNewItems(ArrayList<Charity> charities) {
        array.addAll(charities);
        notifyDataSetChanged();
    }

    public void setOnClickListener(AdapterClickListener<Charity> listener) {
        this.listener = listener;
    }

    @Override
    public void clickElement(Charity item, int elementId) {
        listener.ItemClick(item);
    }
}
