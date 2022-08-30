package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.EventsHolder;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.R;

public class EventsAdapter extends RecyclerView.Adapter implements HolderClickListener<Event> {

    private Context cxt;
    private ArrayList<Event> array;
    private AdapterClickListener<Event> listener;

    public EventsAdapter(Context cxt, ArrayList<Event> array) {
        this.cxt = cxt;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (array == null || array.size() == 0) return;

        Event item = array.get(position);

        if (item != null) {
            ((EventsHolder) holder).Bind(cxt, item);
            ((EventsHolder) holder).setClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return array == null || array.size() == 0 ? 0 : array.size();
    }

    public void addNewItems(ArrayList<Event> events) {
        if (array == null)
            array = new ArrayList<>();

        array.addAll(events);
        notifyDataSetChanged();
    }

    public void clear() {
        if (array == null) return;

        array.clear();
        notifyDataSetChanged();
    }

    public void setOnClickListener(AdapterClickListener<Event> listener) {
        this.listener = listener;
    }

    @Override
    public void clickElement(Event item, int elementId) {
        switch (elementId) {
            case -1: {
                listener.ItemClick(item);
                break;
            }
            case R.id.buy_button: {
                listener.ElementItemClick(item, R.id.buy_button);
                break;
            }
            case R.id.add_celebrity: {
                listener.ElementItemClick(item, R.id.add_celebrity);
                break;
            }
        }
    }

    public void removeElement(Event event) {
        if (array == null) return;

        for (int index = 0; index < array.size(); index++) {
            if (array.get(index).id == event.id) {
                array.remove(index);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void searchEvent(String searchLine) {
        if (array == null || array.size() == 0 || searchLine == null || searchLine.isEmpty()
                || searchLine.length() < 3)
            return;

        ArrayList<Event> searchArray = new ArrayList<>();

        for (int index = 0; index < array.size(); index++) {
            Event event = array.get(index);

            if (event.celebrity.firstName != null &&
                    event.celebrity.firstName.toUpperCase().contains(searchLine.toUpperCase())
                    || event.celebrity.lastName != null &&
                    event.celebrity.lastName.toUpperCase().contains(searchLine.toUpperCase()))
                searchArray.add(event);
            else if (event.shortDescription != null &&
                    event.shortDescription.toUpperCase().contains(searchLine.toUpperCase()))
                searchArray.add(event);
        }

        clear();

        if (searchArray.size() > 0)
            addNewItems(searchArray);
    }
}
