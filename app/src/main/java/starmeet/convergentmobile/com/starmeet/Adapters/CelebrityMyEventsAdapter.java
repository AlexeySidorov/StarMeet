package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

import starmeet.convergentmobile.com.starmeet.Holders.EventCelebrityChildHolder;
import starmeet.convergentmobile.com.starmeet.Holders.EventCelebrityGroupHolder;
import starmeet.convergentmobile.com.starmeet.Listners.AdapterClickListener;
import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.EventCelebrityGroup;
import starmeet.convergentmobile.com.starmeet.Models.OrderModel;
import starmeet.convergentmobile.com.starmeet.R;

public class CelebrityMyEventsAdapter extends BaseExpandableListAdapter implements HolderClickListener<OrderModel> {

    private Context cxt;
    private ArrayList<EventCelebrityGroup> groups;
    private EventCelebrityGroupHolder groupView;
    private EventCelebrityChildHolder childHolder;
    private AdapterClickListener<OrderModel> listener;

    public CelebrityMyEventsAdapter(Context cxt, ArrayList<EventCelebrityGroup> groups) {
        this.cxt = cxt;
        this.groups = groups;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_celebrity_event_group, parent, false);
            groupView = new EventCelebrityGroupHolder(convertView);
            convertView.setTag(R.layout.item_celebrity_event_group, groupView);
        } else
            groupView = (EventCelebrityGroupHolder) convertView.getTag(R.layout.item_celebrity_event_group);

        groupView.setContext(cxt);
        EventCelebrityGroup order = (EventCelebrityGroup) getGroup(groupPosition);
        assert order != null;
        groupView.setTitle(order.getTitle());
        groupView.setStatus(order.getStatus());

        if (isExpanded) {
            groupView.expand();
        } else {
            groupView.collapse();
        }

        groupView.resizeView();

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_celebrity_event_child,
                    parent, false);
            childHolder = new EventCelebrityChildHolder(convertView);
            convertView.setTag(R.layout.item_celebrity_event_child, childHolder);
        } else
            childHolder = (EventCelebrityChildHolder) convertView.getTag(R.layout.item_celebrity_event_child);

        OrderModel order = (OrderModel) getChild(groupPosition, childPosition);
        assert order != null;
        childHolder.Bind(order);
        childHolder.setHolderListener(this);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addNewItems(ArrayList<EventCelebrityGroup> orders) {
        groups.addAll(orders);
        notifyDataSetChanged();
    }

    public void setAdapterListener(AdapterClickListener<OrderModel> listener) {
        this.listener = listener;
    }

    @Override
    public void clickElement(OrderModel item, int elementId) {
        if (item != null)
            listener.ItemClick(item);
    }
}
